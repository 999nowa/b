package net.osmand.googlemaps.addon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Minimal Google Geocoding API client. Runs synchronously and must be called off the UI thread. */
public final class GoogleGeocodingClient {
    private final String apiKey;

    public GoogleGeocodingClient(String apiKey) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
    }

    public JSONArray search(String query, String language, String region) throws Exception {
        if (apiKey.isEmpty()) {
            throw new IllegalStateException("Google Maps API key is not configured");
        }
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encoded
                + "&key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8.name());
        if (language != null && !language.isEmpty()) {
            url += "&language=" + URLEncoder.encode(language, StandardCharsets.UTF_8.name());
        }
        if (region != null && !region.isEmpty()) {
            url += "&region=" + URLEncoder.encode(region, StandardCharsets.UTF_8.name());
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        try {
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                throw new IllegalStateException("Google Geocoding request failed: HTTP " + status);
            }
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            JSONObject json = new JSONObject(response.toString());
            String statusText = json.optString("status", "UNKNOWN_ERROR");
            if (!"OK".equals(statusText) && !"ZERO_RESULTS".equals(statusText)) {
                throw new IllegalStateException("Google Geocoding status: " + statusText);
            }
            return json.optJSONArray("results") != null ? json.getJSONArray("results") : new JSONArray();
        } finally {
            connection.disconnect();
        }
    }
}
