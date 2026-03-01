//
// Created by Augustus Mutinda on 01/03/2026.
//

import Foundation
import BackgroundTasks
import shared

class SyncSchedulerIOS: SyncScheduler {

    func triggerSync() {
        let request = BGProcessingTaskRequest(identifier: "io.pula.surveySync")
        request.requiresNetworkConnectivity = true
        request.requiresExternalPower = false  // optional

        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Could not schedule sync: \(error)")
        }
    }

    // This is called by AppDelegate when the task executes
    func handleSync(task: BGProcessingTask) {
        task.expirationHandler = {
            // Task was killed early
            task.setTaskCompleted(success: false)
        }

        Task {
            let result = await sharedSyncEngine.sync()  // KMM async function
            task.setTaskCompleted(success: result.failed.isEmpty)
            // Reschedule next sync
            triggerSync()
        }
    }
}
