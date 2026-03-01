package io.pula

import android.app.Application
import io.pula.data.AndroidNetworkMonitor
import io.pula.data.setPlatformContext
import io.pula.data.sync.SyncEngine
import io.pula.data.sync.scheduleSync


class PulaApplication : Application() {
    lateinit var syncEngine: SyncEngine

    override fun onCreate() {
        super.onCreate()
        setPlatformContext(this)
        syncEngine = SyncEngine(AndroidNetworkMonitor())

        scheduleSync()
        // UI/ViewModel usage
//        val response = SurveyResponse()
//        appContext.syncEngine.saveOrUploadSurvey(response)
    }
}