package io.pula.data

import android.content.Context
import androidx.work.CoroutineWorker
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