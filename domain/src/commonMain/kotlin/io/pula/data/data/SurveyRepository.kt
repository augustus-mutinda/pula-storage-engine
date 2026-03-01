package io.pula.data.data

import io.pula.data.models.SurveyResponse

interface SurveyRepository {
    suspend fun getPendingResponses(): List<SurveyResponse>
    suspend fun saveSurveyResponse(response: SurveyResponse)
    suspend fun markInProgress(ids: List<String>)
    suspend fun markAsSynced(ids: List<String>)
    suspend fun markAsFailed(ids: List<String>)
    suspend fun getResponsesForFarmer(farmerId: String): List<SurveyResponse>
}