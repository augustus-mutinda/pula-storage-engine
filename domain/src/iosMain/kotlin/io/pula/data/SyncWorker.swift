//
// Created by Augustus Mutinda on 01/03/2026.
//

import Foundation

func scheduleSync() {
    let request = BGProcessingTaskRequest(identifier: "io.pula.sync")
    request.requiresNetworkConnectivity = true
    try? BGTaskScheduler.shared.submit(request)
}

func handleSync(task: BGProcessingTask) {
    Task {
        await sharedSyncEngine.sync()
        task.setTaskCompleted(success: true)
    }
}