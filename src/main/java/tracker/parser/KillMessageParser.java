package tracker.parser;

import tracker.config.KillTrackerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class KillMessageParser {
    private final List<Pattern> killPatterns;

    public KillMessageParser(KillTrackerConfig config) {
        this.killPatterns = new ArrayList<>();
        for (String rule : config.parserRules()) {
            killPatterns.add(Pattern.compile(rule, Pattern.CASE_INSENSITIVE));
        }
    }

    public Optional<KillDetectionResult> parse(String chatLine, String localPlayer) {
        for (Pattern pattern : killPatterns) {
            var matcher = pattern.matcher(chatLine);
            if (!matcher.find()) continue;
            var killer = matcher.group("killer");
            var victim = matcher.group("victim");
            if (killer == null || victim == null || killer.equalsIgnoreCase(victim)) {
                return Optional.of(new KillDetectionResult(victim, victim, true));
            }
            return Optional.of(new KillDetectionResult(killer, victim, false));
        }
        return Optional.empty();
    }
}
