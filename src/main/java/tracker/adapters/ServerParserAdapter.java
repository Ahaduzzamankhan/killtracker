package tracker.adapters;

import java.util.Optional;
import tracker.parser.KillDetectionResult;

public interface ServerParserAdapter {
    Optional<KillDetectionResult> parse(String line);
}
