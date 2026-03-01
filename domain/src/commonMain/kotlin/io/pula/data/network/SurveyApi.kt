package io.pula.data.network

import io.pula.data.models.SurveyResponse
import io.pula.data.models.SyncError

sealed class ApiResult {
    object Success : ApiResult()
    data class Failure(val error: SyncError) : ApiResult()
}

interface SurveyApi {
    suspend fun uploadSurvey(response: SurveyResponse): ApiResult
}