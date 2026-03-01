package io.pula.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import io.pula.data.sync.SyncEngine

class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val engine: SyncEngine
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val result = engine.sync()

        return if (result.stoppedEarly) {
            Result.retry()
        } else {
            Result.success()
        }
    }
}

val request = OneTimeWorkRequestBuilder<SyncWorker>()
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
    )
    .build()

WorkManager.getInstance(context).enqueue(request)