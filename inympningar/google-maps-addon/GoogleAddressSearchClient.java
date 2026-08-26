package net.osmand.googlemaps.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal Google Geocoding HTTP client. It has no dependency on OsmAnd classes,
 * which keeps the feature reusable across upstream updates.
 */
public final class GoogleAddressSearchClient {
    private GoogleAddressSearchClient() {
    }

    public static List<Result> search(@NonNull String query, @NonNull String apiKey) throws IOException {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + URLEncoder.encode(query, StandardCharsets.UTF_8.name())
                + "&key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8.name());

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Google Geocoding HTTP " + connection.getResponseCode());
            }
            String body = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(body);
            JSONArray results = root.optJSONArray("results");
            List<Result> output = new ArrayList<>();
            if (results == null) {
                return output;
            }
            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                JSONObject location = item.getJSONObject("geometry").getJSONObject("location");
                output.add(new Result(
                        item.optString("formatted_address"),
                        location.getDouble("lat"),
                        location.getDouble("lng"),
                        item.optString("place_id", null)));
            }
            return output;
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException("Invalid Google Geocoding response", e);
        } finally {
            connection.disconnect();
        }
    }

    public static final class Result {
        public final String address;
        public final double latitude;
        public final double longitude;
        @Nullable public final String placeId;

        public Result(String address, double latitude, double longitude, @Nullable String placeId) {
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.placeId = placeId;
        }
    }
}
