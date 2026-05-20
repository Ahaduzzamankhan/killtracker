package tracker.models;

import java.time.Instant;
import java.util.UUID;

public record KillEvent(
        UUID id,
        String killer,
        String victim,
        String server,
        Instant timestamp,
        boolean synced
) {
    public static KillEvent create(String killer, String victim, String server) {
        return new KillEvent(UUID.randomUUID(), killer, victim, server, Instant.now(), false);
    }
}
