package io.pula.data

import android.content.Context

lateinit var appContext: Context

actual fun setPlatformContext(context: Any) {
    appContext = context as Context
}

actual fun getPlatformContext(): Any = appContext