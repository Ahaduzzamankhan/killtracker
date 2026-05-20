package tracker.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tracker.KillTracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config", "killtracker.json");

    public KillTrackerConfig load() throws IOException {
        if (Files.notExists(CONFIG_PATH)) {
            Files.createDirectories(CONFIG_PATH.getParent());
            var defaults = defaults();
            Files.writeString(CONFIG_PATH, GSON.toJson(defaults));
            return defaults;
        }
        var loaded = GSON.fromJson(Files.readString(CONFIG_PATH), KillTrackerConfig.class);
        KillTracker.LOGGER.info("Loaded KillTracker config from {}", CONFIG_PATH);
        return loaded;
    }

    private KillTrackerConfig defaults() {
        return new KillTrackerConfig(
                "https://firestore.googleapis.com/v1/projects",
                "replace-with-project-id",
                "",
                30,
                false,
                5000,
                "config/killtracker.db",
                List.of(
                        "(?<killer>\\w+) killed (?<victim>\\w+)",
                        "(?<victim>\\w+) was slain by (?<killer>\\w+)",
                        "(?<killer>\\w+) eliminated (?<victim>\\w+)"
                )
        );
    }
}
