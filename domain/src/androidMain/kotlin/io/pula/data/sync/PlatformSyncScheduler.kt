package io.pula.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.pula.data.SyncWorker
import io.pula.data.getPlatformContext

actual class PlatformSyncScheduler : SyncScheduler {
    actual override fun triggerSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()


        WorkManager
            .getInstance(getPlatformContext() as Context)
            .enqueueUniqueWork(
                "survey_sync",
                ExistingWorkPolicy.KEEP,
                request
            )
    }
}