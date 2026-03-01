import io.pula.data.data.SurveyRepository
import io.pula.data.models.SurveyResponse

class FakeSurveyRepository(
    private val initialPending: List<SurveyResponse>
) : SurveyRepository {

    private val pending = initialPending.toMutableList()
    val synced = mutableListOf<String>()
    val failed = mutableListOf<String>()
    val inProgress = mutableListOf<String>()

    override suspend fun getPendingResponses(): List<SurveyResponse> = pending

    override suspend fun markInProgress(ids: List<String>) {
        inProgress.addAll(ids)
    }

    override suspend fun markAsSynced(ids: List<String>) {
        synced.addAll(ids)
    }

    override suspend fun markAsFailed(ids: List<String>) {
        failed.addAll(ids)
    }

    override suspend fun getResponsesForFarmer(farmerId: String): List<SurveyResponse> = pending

    override suspend fun saveSurveyResponse(survey: SurveyResponse) {
        pending.add(survey)
    }
}