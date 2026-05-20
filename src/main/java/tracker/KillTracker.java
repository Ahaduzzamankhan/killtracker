package tracker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tracker.config.ConfigManager;
import tracker.database.SQLiteManager;
import tracker.events.ChatEventHandler;
import tracker.firebase.FirebaseClient;
import tracker.services.StatsService;
import tracker.sync.SyncManager;

public final class KillTracker implements ClientModInitializer {
    public static final String MOD_ID = "killtracker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private SyncManager syncManager;

    @Override
    public void onInitializeClient() {
        try {
            var configManager = new ConfigManager();
            var config = configManager.load();
            var db = new SQLiteManager(config.databasePath());
            db.initialize();

            var firebaseClient = new FirebaseClient(config);
            var statsService = new StatsService(db);
            var chatEventHandler = new ChatEventHandler(statsService, config);
            this.syncManager = new SyncManager(db, firebaseClient, config);

            ClientReceiveMessageEvents.GAME.register((message, overlay) ->
                    chatEventHandler.handleChatLine(message.getString()));

            syncManager.start();
            LOGGER.info("KillTracker initialized");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize KillTracker", e);
        }
    }
}
