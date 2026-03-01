package io.pula.data

import io.ktor.client.engine.HttpClientEngine
import surveydb.SurveyDatabase

expect fun setPlatformContext(context: Any)
expect fun getPlatformContext(): Any

expect fun databaseProvider(): SurveyDatabase

expect fun httpClient(): HttpClientEngine