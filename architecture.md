# Architecture Overview

### a. Chosen Architecture and Alternatives Considered

The `domain` module holds all core functionality: data persistence, business logic, network communication, models, and platform-specific tools like network monitoring and sync scheduling.

I implemented a **layered logical architecture** within this module:

1. **Models** – Core data entities such as `SurveyResponse`, `SyncStatus`, and `SyncError`.
2. **Repository / Business Layer** – `SurveyRepository` handles local storage (SQLDelight) and exposes data to the sync engine.
3. **Sync Engine** – Orchestrates offline-first behavior, queue processing, partial failure handling, and network-aware retries.
4. **Network Layer** – Ktor-based `SurveyApi` for multiplatform network requests.
5. **Platform Tools** – Abstractions for platform-specific capabilities: `NetworkMonitor`, `SyncScheduler`. Android and iOS provide concrete implementations.

This approach was chosen because:

- **Offline-first**: Data is always persisted locally; network availability only triggers uploads.
- **Cross-platform**: Core logic is pure Kotlin, enabling JVM tests and iOS/Android reuse.
- **Testability**: By separating concerns and injecting dependencies, we can unit test the engine and repository independently.

**Alternatives considered**:

- Tightly coupling repository and network logic — rejected due to difficulty handling intermittent connectivity.
- Using WorkManager only — Android-only, cannot cover iOS.
- Reactive Flow-based sync — unnecessary complexity for an MVP; a queue-driven engine suffices.

---

### b. Handling Media File Uploads with Compression

Photos are stored locally as file paths in `SurveyResponse`. To handle uploads:

1. Introduce a **MediaProcessor** interface to compress images before sending.
2. The sync engine would compress each photo asynchronously using coroutines.
3. Large files can be uploaded in chunks to reduce memory footprint.
4. Compression and upload logic is injected, allowing platform-specific optimizations (e.g., iOS vs Android).

---

### c. Network Detection Edge Cases

The network monitor could fail in scenarios such as:

- Connected to WiFi without actual internet access.
- Mobile signal fluctuates mid-sync.

Mitigation:

- Perform lightweight test requests before starting sync.
- Use exponential backoff on repeated failures.
- Track and log network status for diagnostics.

---

### d. Remote Troubleshooting Support

For remote debugging without device access:

- **Sync logs** – Track survey IDs, timestamps, status changes, retry attempts, and error codes.
- **Device context** – Battery, storage, and network type metadata can be optionally logged.
- **Error categorization** – Distinguish transient errors (`NetworkUnavailable`, `Timeout`) from permanent ones (`ClientError`).
- Logs can be synced asynchronously when network is available for support analysis.

---

### e. Geospatial Data Considerations

Adding GPS-based field boundaries presents challenges:

- Low satellite coverage or obstructed signal causes inaccuracies.
- Device variability leads to inconsistent readings.
- Intermittent GPS updates may require smoothing or interpolation.

Validation strategies:

- Capture multiple redundant points per boundary vertex.
- Cross-check against reference points or satellite imagery.
- Store GPS accuracy metrics (HDOP/PDOP) to assess confidence.
- Cache points locally for later correction if needed.

---

### f. One Thing I Would Do Differently with More Time

Implement a **device-aware sync strategy**:

- Adjust uploads based on battery level, storage, or network type.
- For example, defer media uploads on low battery or switch to compressed payloads on metered networks.
- This would optimize reliability, save battery, and improve user experience in low-resource regions.