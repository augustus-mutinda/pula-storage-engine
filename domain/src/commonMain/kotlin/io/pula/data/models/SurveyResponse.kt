package io.pula.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SurveyResponse(
    val id: String,
    val farmerId: String,
    val payloadJson: String,
    val createdAt: Long,
    val retryCount: Int,
    val status: SyncStatus
)