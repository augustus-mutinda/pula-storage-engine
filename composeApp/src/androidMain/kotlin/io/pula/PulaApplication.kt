package io.pula

import android.app.Application
import io.pula.data.setPlatformContext

class PulaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setPlatformContext(this)
    }
}