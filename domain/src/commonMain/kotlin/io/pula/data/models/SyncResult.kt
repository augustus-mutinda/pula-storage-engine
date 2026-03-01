package io.pula.data.models

data class SyncResult(
    val succeeded: List<String>,
    val failed: List<String>,
    val stoppedEarly: Boolean,
    val reason: String?
)