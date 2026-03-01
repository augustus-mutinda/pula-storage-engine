package io.pula.data

import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import surveydb.SurveyDatabase

actual fun databaseProvider(): SurveyDatabase {
    val driver = NativeSqliteDriver(SurveyDatabase.Schema, "survey.db")
    return SurveyDatabase(driver)
}

actual fun httpClient(): HttpClientEngine = Darwin.create()