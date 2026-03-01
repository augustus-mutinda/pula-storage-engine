import io.pula.data.models.SurveyResponse
import io.pula.data.models.SyncStatus

fun testSurvey(id: String): SurveyResponse {
    return SurveyResponse(
        id = id,
        farmerId = "farmer-$id",
        answers = emptyList(),
        status = SyncStatus.PENDING,
        mediaPaths = emptyList()
    )
}