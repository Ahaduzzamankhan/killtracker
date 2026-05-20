package tracker.models;

public record SyncQueueItem(long id, String payload, String type, boolean synced) {}
