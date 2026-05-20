package tracker.services;

import com.google.gson.Gson;
import tracker.database.SQLiteManager;
import tracker.models.KillEvent;

import java.sql.SQLException;

public final class StatsService {
    private static final Gson GSON = new Gson();
    private final SQLiteManager db;

    public StatsService(SQLiteManager db) { this.db = db; }

    public void recordKill(String killer, String victim, String server) throws SQLException {
        var event = KillEvent.create(killer, victim, server);
        db.saveKillEvent(event, GSON.toJson(event));
    }

    public void recordDeath(String victim, String killer, String server) throws SQLException {
        recordKill(killer == null ? "unknown" : killer, victim, server);
    }
}
