package io.pula.data.data

import io.pula.data.models.SurveyResponse

interface SurveyRepository {
    suspend fun getPending(): List<SurveyResponse>
    suspend fun markInProgress(id: String)
    suspend fun markSynced(id: String)
    suspend fun markFailed(id: String, retry: Boolean)
}