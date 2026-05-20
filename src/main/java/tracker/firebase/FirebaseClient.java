package tracker.firebase;

import okhttp3.*;
import tracker.config.KillTrackerConfig;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public final class FirebaseClient {
    private final OkHttpClient http;
    private final KillTrackerConfig config;

    public FirebaseClient(KillTrackerConfig config) {
        this.config = config;
        this.http = new OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(15))
                .build();
    }

    public boolean uploadBatch(List<String> payloads) throws IOException {
        if (payloads.isEmpty() || config.authToken().isBlank()) return false;
        String endpoint = String.format("%s/%s/databases/(default)/documents:batchWrite", config.firebaseEndpoint(), config.firebaseProjectId());
        Request request = new Request.Builder()
                .url(endpoint)
                .addHeader("Authorization", "Bearer " + config.authToken())
                .post(RequestBody.create(String.join("\n", payloads), MediaType.parse("application/json")))
                .build();
        try (Response response = http.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public boolean isInternetAvailable() {
        Request request = new Request.Builder().url("https://www.google.com/generate_204").get().build();
        try (Response response = http.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }
}
