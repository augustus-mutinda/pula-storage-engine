package io.pula.data.sync

import io.pula.data.data.SurveyRepository
import io.pula.data.models.SyncError
import io.pula.data.models.SyncResult
import io.pula.data.network.ApiResult
import io.pula.data.network.NetworkMonitor
import io.pula.data.network.SurveyApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SyncEngine(
    private val repository: SurveyRepository,
    private val api: SurveyApi,
    private val networkMonitor: NetworkMonitor
) {
    private val mutex = Mutex()

    suspend fun sync(): SyncResult = mutex.withLock {

        if (!networkMonitor.isLikelyAvailable()) {
            return SyncResult(
                emptyList(),
                emptyList(),
                stoppedEarly = true,
                reason = "No network"
            )
        }

        val pending = repository.getPending()

        if (pending.isEmpty()) {
            return SyncResult(emptyList(), emptyList(), false, null)
        }

        val succeeded = mutableListOf<String>()
        val failed = mutableListOf<String>()

        for (survey in pending) {

            if (!networkMonitor.isLikelyAvailable()) {
                return SyncResult(
                    succeeded,
                    failed,
                    stoppedEarly = true,
                    reason = "Network lost mid-sync"
                )
            }

            repository.markInProgress(survey.id)

            when (val result = api.uploadSurvey(survey)) {

                is ApiResult.Success -> {
                    repository.markSynced(survey.id)
                    succeeded.add(survey.id)
                }

                else -> {
                    val error = (result as ApiResult.Failure).error

                    repository.markFailed(
                        survey.id,
                        retry = error.shouldRetry
                    )

                    failed.add(survey.id)

                    if (error is SyncError.NetworkUnavailable ||
                        error is SyncError.Timeout
                    ) {
                        return SyncResult(
                            succeeded,
                            failed,
                            stoppedEarly = true,
                            reason = "Network degradation"
                        )
                    }
                }
            }
        }

        SyncResult(succeeded, failed, false, null)
    }
}