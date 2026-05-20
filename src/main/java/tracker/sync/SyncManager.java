package tracker.sync;

import tracker.KillTracker;
import tracker.config.KillTrackerConfig;
import tracker.database.SQLiteManager;
import tracker.firebase.FirebaseClient;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class SyncManager {
    private final SQLiteManager db;
    private final FirebaseClient firebaseClient;
    private final KillTrackerConfig config;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private int failures;

    public SyncManager(SQLiteManager db, FirebaseClient firebaseClient, KillTrackerConfig config) {
        this.db = db;
        this.firebaseClient = firebaseClient;
        this.config = config;
    }

    public void start() {
        executor.scheduleWithFixedDelay(this::syncOnce, 5, config.syncIntervalSeconds(), TimeUnit.SECONDS);
    }

    private void syncOnce() {
        try {
            if (!firebaseClient.isInternetAvailable()) return;
            var pending = db.unsyncedQueue(100);
            var payloads = new ArrayList<String>();
            var ids = new ArrayList<Long>();
            for (var item : pending) {
                payloads.add(item.payload());
                ids.add(item.id());
            }
            if (payloads.isEmpty()) return;
            if (firebaseClient.uploadBatch(payloads)) {
                db.markSynced(ids);
                failures = 0;
            } else {
                failures++;
            }
        } catch (Exception e) {
            failures++;
            KillTracker.LOGGER.warn("Sync failed (attempt {}): {}", failures, e.getMessage());
        }
        long backoff = Math.min(300, (long) Math.pow(2, Math.min(6, failures)));
        if (failures > 0) {
            try {
                Thread.sleep(backoff * 1000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
