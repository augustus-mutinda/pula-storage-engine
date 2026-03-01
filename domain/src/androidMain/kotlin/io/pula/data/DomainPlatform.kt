package io.pula.data

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import surveydb.SurveyDatabase

lateinit var appContext: Context

actual fun setPlatformContext(context: Any) {
    appContext = context as Context
}

actual fun getPlatformContext(): Any = appContext

actual fun databaseProvider(): SurveyDatabase {
    val driver =
        AndroidSqliteDriver(SurveyDatabase.Schema, getPlatformContext() as Context, "survey.db")
    return SurveyDatabase(driver)
}

actual fun httpClient(): HttpClientEngine = OkHttp.create()