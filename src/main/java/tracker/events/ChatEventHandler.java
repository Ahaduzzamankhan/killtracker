package tracker.events;

import tracker.KillTracker;
import tracker.config.KillTrackerConfig;
import tracker.parser.KillMessageParser;
import tracker.services.StatsService;

public final class ChatEventHandler {
    private final StatsService statsService;
    private final KillMessageParser parser;

    public ChatEventHandler(StatsService statsService, KillTrackerConfig config) {
        this.statsService = statsService;
        this.parser = new KillMessageParser(config);
    }

    public void handleChatLine(String line) {
        parser.parse(line, "localPlayer").ifPresent(result -> {
            try {
                statsService.recordKill(result.killer(), result.victim(), "unknown-server");
            } catch (Exception e) {
                KillTracker.LOGGER.error("Failed handling kill line: {}", line, e);
            }
        });
    }
}
