package io.pula.data.data

import io.pula.data.databaseProvider
import io.pula.data.models.SurveyResponse
import io.pula.data.models.SyncStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class SurveyRepositoryImpl : SurveyRepository {
    private val db = databaseProvider()

    // Ensure thread safety
    private val mutex = Mutex()

    override suspend fun saveSurveyResponse(response: SurveyResponse) {
        mutex.withLock {
            withContext(Dispatchers.Default) {
                db.survey_responseQueries.insertSurveyResponse(
                    id = response.id,
                    farmerId = response.farmerId,
                    answersJson = Json.encodeToString(SurveyResponse.serializer(), response),
                    status = response.status.name,
                    timestamp = response.timestamp.toString()
                )
            }
        }
    }

    override suspend fun getPendingResponses(): List<SurveyResponse> {
        return mutex.withLock {
            withContext(Dispatchers.Default) {
                db.survey_responseQueries.getResponsesByStatus(SyncStatus.PENDING.name)
                    .executeAsList()
                    .map { Json.decodeFromString(SurveyResponse.serializer(), it.answersJson) }
            }
        }
    }

    override suspend fun markAsSynced(ids: List<String>) {
        mutex.withLock {
            withContext(Dispatchers.Default) {
                ids.forEach { id ->
                    db.survey_responseQueries.updateStatus(id, SyncStatus.SYNCED.name)
                }
            }
        }
    }

    override suspend fun markAsFailed(ids: List<String>) {
        mutex.withLock {
            withContext(Dispatchers.Default) {
                ids.forEach { id ->
                    db.survey_responseQueries.updateStatus(id, SyncStatus.FAILED.name)
                }
            }
        }
    }

    override suspend fun getResponsesForFarmer(farmerId: String): List<SurveyResponse> {
        return mutex.withLock {
            withContext(Dispatchers.Default) {
                db.survey_responseQueries.getResponsesByFarmer(farmerId)
                    .executeAsList()
                    .map { Json.decodeFromString(SurveyResponse.serializer(), it.answersJson) }
            }
        }
    }

    override suspend fun markInProgress(ids: List<String>) = mutex.withLock {
        withContext(Dispatchers.Default) {
            db.transaction {
                ids.forEach { id ->
                    db.survey_responseQueries.updateStatus("IN_PROGRESS", id)
                }
            }
        }
    }
}