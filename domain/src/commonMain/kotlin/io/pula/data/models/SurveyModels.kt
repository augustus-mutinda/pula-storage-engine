package io.pula.data.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SurveyAnswer(
    val questionId: String,
    val answer: String
)

@Serializable
data class RepeatingSection(
    val sectionId: String,
    val answers: List<SurveyAnswer>
)

@Serializable
data class SurveyResponse(
    val id: String, // UUID
    val farmerId: String,
    val answers: List<SurveyAnswer>,
    val repeatingSections: List<RepeatingSection> = emptyList(),
    val mediaPaths: List<String> = emptyList(), // local file paths
    val timestamp: Instant = Clock.System.now(),
    val status: SyncStatus = SyncStatus.PENDING
)

enum class SyncStatus {
    PENDING,
    SYNCED,
    FAILED
}