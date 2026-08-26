package net.osmand.googlemaps.addon;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Creates and caches a Google Map Tiles API 2D session. */
public final class GoogleMapTilesSession {
    private static final String ENDPOINT = "https://tile.googleapis.com/v1/createSession?key=";

    private final String apiKey;
    private final String language;
    private final String region;
    private String session;
    private long expiryMillis;

    public GoogleMapTilesSession(String apiKey, String language, String region) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.language = language == null ? "en-US" : language;
        this.region = region == null ? "SE" : region;
    }

    public synchronized String getSession(String mapType) throws Exception {
        if (session != null && System.currentTimeMillis() < expiryMillis - 60_000L) {
            return session;
        }
        if (apiKey.isEmpty()) {
            throw new IllegalStateException("Google Maps API key is not configured");
        }
        JSONObject request = new JSONObject();
        request.put("mapType", mapType);
        request.put("language", language);
        request.put("region", region);
        HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT + apiKey).openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        byte[] body = request.toString().getBytes(StandardCharsets.UTF_8);
        try (OutputStream output = connection.getOutputStream()) { output.write(body); }
        int status = connection.getResponseCode();
        if (status < 200 || status >= 300) {
            throw new IllegalStateException("Google Map Tiles session request failed: HTTP " + status);
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) response.append(line);
        } finally { connection.disconnect(); }
        JSONObject json = new JSONObject(response.toString());
        session = json.getString("session");
        long expirySeconds = Long.parseLong(json.optString("expiry", "0"));
        expiryMillis = expirySeconds > 0 ? expirySeconds * 1000L : System.currentTimeMillis() + 24L * 60L * 60L * 1000L;
        return session;
    }

    public String getTileUrl(String mapType, int zoom, int x, int y) throws Exception {
        String token = getSession(mapType);
        return "https://tile.googleapis.com/v1/2dtiles/" + zoom + "/" + x + "/" + y
                + "?session=" + token + "&key=" + apiKey;
    }
}
