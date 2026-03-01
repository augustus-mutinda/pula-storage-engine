package io.pula.data.models

enum class SyncStatus {
    PENDING,
    IN_PROGRESS,
    SYNCED,
    FAILED_TEMPORARY,
    FAILED_PERMANENT
}