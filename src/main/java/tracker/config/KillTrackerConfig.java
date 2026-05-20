package tracker.config;

import java.util.List;

public record KillTrackerConfig(
        String firebaseEndpoint,
        String firebaseProjectId,
        String authToken,
        long syncIntervalSeconds,
        boolean debugLogging,
        int offlineCacheSize,
        String databasePath,
        List<String> parserRules
) {}
