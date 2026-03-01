//
// Created by Augustus Mutinda on 01/03/2026.
//

import Foundation

class IOSNetworkMonitor: NetworkMonitor {
    private let monitor = NWPathMonitor()
    private var available = false

    init() {
        monitor.pathUpdateHandler = { path in
            self.available = path.status == .satisfied
        }
        monitor.start(queue: DispatchQueue.global())
    }

    func isLikelyAvailable() -> Bool {
        return available
    }
}
