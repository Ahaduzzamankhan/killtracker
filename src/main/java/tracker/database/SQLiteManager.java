package tracker.database;

import tracker.KillTracker;
import tracker.models.KillEvent;
import tracker.models.SyncQueueItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class SQLiteManager {
    private final String jdbcUrl;

    public SQLiteManager(String databasePath) {
        this.jdbcUrl = "jdbc:sqlite:" + databasePath;
    }

    public void initialize() throws SQLException {
        try (Connection c = connection(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS player_stats (uuid TEXT PRIMARY KEY, username TEXT, kills INTEGER NOT NULL, deaths INTEGER NOT NULL, kdr REAL NOT NULL, updated_at TEXT NOT NULL)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS kill_history (id TEXT PRIMARY KEY, killer TEXT NOT NULL, victim TEXT NOT NULL, server TEXT NOT NULL, timestamp TEXT NOT NULL, synced INTEGER NOT NULL DEFAULT 0)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS pending_sync_queue (id INTEGER PRIMARY KEY AUTOINCREMENT, payload TEXT NOT NULL, type TEXT NOT NULL, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, synced INTEGER NOT NULL DEFAULT 0)");
        }
    }

    public synchronized Connection connection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    public synchronized void saveKillEvent(KillEvent event, String payload) throws SQLException {
        try (Connection c = connection()) {
            try (PreparedStatement ps = c.prepareStatement("INSERT OR IGNORE INTO kill_history(id,killer,victim,server,timestamp,synced) VALUES(?,?,?,?,?,?)")) {
                ps.setString(1, event.id().toString());
                ps.setString(2, event.killer());
                ps.setString(3, event.victim());
                ps.setString(4, event.server());
                ps.setString(5, event.timestamp().toString());
                ps.setInt(6, event.synced() ? 1 : 0);
                ps.executeUpdate();
            }
            enqueue(c, payload, "kill_event");
        }
    }

    private void enqueue(Connection c, String payload, String type) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO pending_sync_queue(payload,type,synced) VALUES(?,?,0)")) {
            ps.setString(1, payload);
            ps.setString(2, type);
            ps.executeUpdate();
        }
    }

    public synchronized List<SyncQueueItem> unsyncedQueue(int limit) throws SQLException {
        var list = new ArrayList<SyncQueueItem>();
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement("SELECT id,payload,type,synced FROM pending_sync_queue WHERE synced=0 ORDER BY id LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SyncQueueItem(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getInt(4) == 1));
                }
            }
        }
        return list;
    }

    public synchronized void markSynced(List<Long> ids) throws SQLException {
        if (ids.isEmpty()) return;
        String sql = "UPDATE pending_sync_queue SET synced=1 WHERE id=?";
        try (Connection c = connection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (Long id : ids) {
                ps.setLong(1, id);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
