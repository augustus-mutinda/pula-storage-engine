package io.pula.data.models

sealed class SyncError {
    object NetworkUnavailable : SyncError()
    object Timeout : SyncError()
    data class ServerError(val code: Int) : SyncError()
    object ClientError : SyncError()
    object Unknown : SyncError()

    val shouldRetry: Boolean
        get() = when (this) {
            NetworkUnavailable,
            Timeout,
            is ServerError -> true
            ClientError -> false
            Unknown -> true
        }
}