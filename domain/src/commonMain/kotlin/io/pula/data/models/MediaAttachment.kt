package io.pula.data.models

data class MediaAttachment(
    val id: String,
    val surveyId: String,
    val relativePath: String,
    val uploaded: Boolean
)