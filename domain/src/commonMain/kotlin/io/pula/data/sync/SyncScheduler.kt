package io.pula.data.sync

interface SyncScheduler {
    fun triggerSync()
}

expect class PlatformSyncScheduler() : SyncScheduler {
    override fun triggerSync()
}