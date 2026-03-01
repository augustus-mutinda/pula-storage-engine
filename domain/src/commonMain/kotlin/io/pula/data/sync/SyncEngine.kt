package io.pula.data.sync

import io.pula.data.data.SurveyRepository
import io.pula.data.data.SurveyRepositoryImpl
import io.pula.data.models.SurveyResponse
import io.pula.data.models.SyncError
import io.pula.data.models.SyncResult
import io.pula.data.models.SyncStatus
import io.pula.data.network.ApiResult
import io.pula.data.network.NetworkMonitor
import io.pula.data.network.SurveyApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SyncEngine(
    private val networkMonitor: NetworkMonitor,
    private val surveyApi: SurveyApi = SurveyApi(),
    private val repository: SurveyRepository = SurveyRepositoryImpl()
) {
    private val mutex = Mutex()

    /**
     * Sync all pending surveys.
     * Stops early if network is lost or degraded.
     */
    suspend fun sync(): SyncResult = mutex.withLock {

        if (!networkMonitor.isAvailableForSync()) {
            return SyncResult(
                succeeded = emptyList(),
                failed = emptyList(),
                stoppedEarly = true,
                reason = "No network available"
            )
        }

        val pending = repository.getPendingResponses()

        if (pending.isEmpty()) {
            return SyncResult(
                succeeded = emptyList(),
                failed = emptyList(),
                stoppedEarly = false,
                reason = null
            )
        }

        val succeeded = mutableListOf<String>()
        val failed = mutableListOf<String>()

        for (survey in pending) {

            // Check network mid-sync
            if (!networkMonitor.isAvailableForSync()) {
                return SyncResult(
                    succeeded = succeeded,
                    failed = failed,
                    stoppedEarly = true,
                    reason = "Network lost mid-sync"
                )
            }

            // Mark as in-progress
            repository.markInProgress(listOf(survey.id))

            // Attempt upload
            when (val result = surveyApi.uploadSurvey(survey)) {

                is ApiResult.Success -> {
                    repository.markAsSynced(listOf(survey.id))
                    succeeded.add(survey.id)
                }

                is ApiResult.Failure -> {
                    val error = result.error

                    repository.markAsFailed(listOf(survey.id))
                    failed.add(survey.id)

                    // Stop early if network-related error
                    if (error is SyncError.NetworkUnavailable || error is SyncError.Timeout) {
                        return SyncResult(
                            succeeded = succeeded,
                            failed = failed,
                            stoppedEarly = true,
                            reason = "Network degradation"
                        )
                    }
                }
            }
        }

        // Finished syncing
        return SyncResult(
            succeeded = succeeded,
            failed = failed,
            stoppedEarly = false,
            reason = null
        )
    }

    /**
     * Offline-first save locally, mark as pending for next sync. If network is available, can trigger immediate sync.
     */
    suspend fun saveOrUploadSurvey(survey: SurveyResponse) {
        repository.saveSurveyResponse(survey.copy(status = SyncStatus.PENDING))
    }
}