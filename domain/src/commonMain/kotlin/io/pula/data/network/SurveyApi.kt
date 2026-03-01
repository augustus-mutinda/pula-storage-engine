package io.pula.data.network

import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.errors.IOException
import io.pula.data.models.SurveyResponse
import io.pula.data.models.SyncError
import io.pula.data.network.Api.baseUrl
import io.pula.data.network.NetworkClient.client

sealed class ApiResult {
    object Success : ApiResult()
    data class Failure(val error: SyncError) : ApiResult()
}

open class SurveyApi() {

    open suspend fun uploadSurvey(survey: SurveyResponse): ApiResult {
        return try {
            val response: HttpResponse = client.post("$baseUrl/surveys") {
                contentType(ContentType.Application.Json)
                setBody(survey)
            }

            when (response.status.value) {
                in 200..299 -> ApiResult.Success
                in 500..599 -> ApiResult.Failure(SyncError.ServerError(response.status.value))
                in 400..499 -> ApiResult.Failure(SyncError.ClientError)
                else -> ApiResult.Failure(SyncError.Unknown)
            }
        } catch (e: SocketTimeoutException) {
            ApiResult.Failure(SyncError.Timeout)
        } catch (e: IOException) {
            ApiResult.Failure(SyncError.NetworkUnavailable)
        } catch (e: Exception) {
            ApiResult.Failure(SyncError.Unknown)
        }
    }
}