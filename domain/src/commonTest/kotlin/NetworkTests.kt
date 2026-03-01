import io.pula.data.models.SyncError
import io.pula.data.network.ApiResult
import io.pula.data.sync.SyncEngine
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NetworkTests {

    @Test
    fun `sync stops when no network`() = runTest {
        val network = FakeNetworkMonitor(false)
        val api = FakeSurveyApi(ApiResult.Success)
        val repo = FakeSurveyRepository(emptyList())

        val engine = SyncEngine(network, api, repo)

        val result = engine.sync()

        assertTrue(result.stoppedEarly)
        assertEquals("No network available", result.reason)
    }

    @Test
    fun `successful sync`() = runTest {
        val survey = testSurvey("1")

        val network = FakeNetworkMonitor(true)
        val api = FakeSurveyApi(ApiResult.Success)
        val repo = FakeSurveyRepository(listOf(survey))

        val engine = SyncEngine(network, api, repo)

        val result = engine.sync()

        assertEquals(listOf("1"), result.succeeded)
        assertEquals(1, repo.synced.size)
    }

    @Test
    fun `partial success across multiple surveys`() = runTest {
        val surveys = listOf(
            testSurvey("1"),
            testSurvey("2")
        )

        val api = SequencedSurveyApi(
            listOf(
                ApiResult.Success,
                ApiResult.Failure(SyncError.ServerError(500))
            )
        )

        val network = FakeNetworkMonitor(true)
        val repo = FakeSurveyRepository(surveys)
        val engine = SyncEngine(network, api, repo)

        val result = engine.sync()

        assertEquals(listOf("1"), result.succeeded)
        assertEquals(listOf("2"), result.failed)
        assertEquals(false, result.stoppedEarly)
    }

    @Test
    fun `network lost mid sync`() = runTest {
        val surveys = listOf(
            testSurvey("1"),
            testSurvey("2")
        )

        val network = FlakyNetworkMonitor(failAfterCalls = 1)
        val api = FakeSurveyApi(ApiResult.Success)
        val repo = FakeSurveyRepository(surveys)

        val engine = SyncEngine(network, api, repo)

        val result = engine.sync()

        assertTrue(result.stoppedEarly)
    }

    @Test
    fun `timeout stops sync early`() = runTest {
        val survey = testSurvey("1")

        val network = FakeNetworkMonitor(true)
        val api = FakeSurveyApi(
            ApiResult.Failure(SyncError.Timeout)
        )

        val repo = FakeSurveyRepository(listOf(survey))
        val engine = SyncEngine(network, api, repo)

        val result = engine.sync()

        assertTrue(result.stoppedEarly)
        assertEquals("Network degradation", result.reason)
    }
}