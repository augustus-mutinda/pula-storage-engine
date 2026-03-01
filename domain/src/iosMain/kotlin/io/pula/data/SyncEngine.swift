//
// Created by Augustus Mutinda on 01/03/2026.
//

import Foundation

// MARK: - Models

struct SurveyResponse: Identifiable {
    let id: String
    let farmerId: String
    let answers: [String: Any] // simplified
    var mediaPaths: [String]
    var status: SyncStatus
    let timestamp: Date
}

enum SyncStatus {
    case pending
    case inProgress
    case synced
    case failed
}

enum SyncError: Error {
    case networkUnavailable
    case timeout
    case serverError(code: Int)
    case clientError
    case unknown

    var shouldRetry: Bool {
        switch self {
        case .networkUnavailable, .timeout, .serverError:
            return true
        case .clientError:
            return false
        case .unknown:
            return true
        }
    }
}

enum ApiResult {
    case success
    case failure(SyncError)
}

struct SyncResult {
    let succeeded: [String]
    let failed: [String]
    let stoppedEarly: Bool
    let reason: String?
}

// MARK: - Protocols

protocol SurveyRepository {
    func getPendingResponses() async -> [SurveyResponse]
    func markInProgress(_ ids: [String]) async
    func markAsSynced(_ ids: [String]) async
    func markAsFailed(_ ids: [String]) async
}

protocol SurveyApi {
    func uploadSurvey(_ survey: SurveyResponse) async -> ApiResult
}

protocol NetworkMonitor {
    func isAvailableForSync() -> Bool
}

// MARK: - Sync Engine Actor

actor SyncEngine {

    private let repository: SurveyRepository
    private let api: SurveyApi
    private let networkMonitor: NetworkMonitor
    private var isSyncRunning = false

    init(repository: SurveyRepository, api: SurveyApi, networkMonitor: NetworkMonitor) {
        self.repository = repository
        self.api = api
        self.networkMonitor = networkMonitor
    }

    func sync() async -> SyncResult {
        if isSyncRunning {
            return SyncResult(succeeded: [], failed: [], stoppedEarly: true, reason: "Sync already in progress")
        }

        isSyncRunning = true
        defer { isSyncRunning = false }

        guard networkMonitor.isAvailableForSync() else {
            return SyncResult(succeeded: [], failed: [], stoppedEarly: true, reason: "No network available")
        }

        let pending = await repository.getPendingResponses()
        if pending.isEmpty {
            return SyncResult(succeeded: [], failed: [], stoppedEarly: false, reason: nil)
        }

        var succeeded: [String] = []
        var failed: [String] = []

        for survey in pending {
            guard networkMonitor.isAvailableForSync() else {
                return SyncResult(succeeded: succeeded, failed: failed, stoppedEarly: true, reason: "Network lost mid-sync")
            }

            await repository.markInProgress([survey.id])

            let result = await api.uploadSurvey(survey)

            switch result {
            case .success:
                await repository.markAsSynced([survey.id])
                succeeded.append(survey.id)
            case .failure(let error):
                await repository.markAsFailed([survey.id])
                failed.append(survey.id)

                if error.shouldRetry {
                    return SyncResult(succeeded: succeeded, failed: failed, stoppedEarly: true, reason: "Network degradation")
                }
            }
        }

        return SyncResult(succeeded: succeeded, failed: failed, stoppedEarly: false, reason: nil)
    }
}