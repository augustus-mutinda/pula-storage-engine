import io.pula.data.models.SurveyResponse
import io.pula.data.network.ApiResult
import io.pula.data.network.SurveyApi

class FakeSurveyApi(
    private val result: ApiResult
) : SurveyApi() {

    override suspend fun uploadSurvey(survey: SurveyResponse): ApiResult {
        return result
    }
}

class SequencedSurveyApi(
    private val results: List<ApiResult>
) : SurveyApi() {

    private var index = 0

    override suspend fun uploadSurvey(survey: SurveyResponse): ApiResult {
        return results.getOrElse(index++) { results.last() }
    }
}