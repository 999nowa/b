package net.osmand.plus.googlemaps;

import android.net.Uri;

import androidx.annotation.NonNull;

import net.osmand.plus.OsmandApplication;
import net.osmand.util.Algorithms;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Lightweight Google Geocoding API client used as an optional search provider. */
public class GoogleAddressSearchClient {
    public static class Result {
        public String name;
        public String address;
        public double latitude;
        public double longitude;
        public String placeId;
    }

    private final OsmandApplication app;

    public GoogleAddressSearchClient(@NonNull OsmandApplication app) {
        this.app = app;
    }

    @NonNull
    public List<Result> search(@NonNull String query) throws Exception {
        String apiKey = GoogleMapsPreferences.getApiKey(app);
        if (Algorithms.isEmpty(apiKey) || Algorithms.isEmpty(query)) {
            return new ArrayList<>();
        }
        Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/geocode/json")
                .buildUpon().appendQueryParameter("address", query)
                .appendQueryParameter("key", apiKey).build();
        HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setRequestMethod("GET");
        try {
            int code = connection.getResponseCode();
            InputStream stream = code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
            if (stream == null) return new ArrayList<>();
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) body.append(line);
            }
            if (code < 200 || code >= 300) throw new IllegalStateException("Google Geocoding HTTP " + code);
            return parseResults(new JSONObject(body.toString()));
        } finally {
            connection.disconnect();
        }
    }

    @NonNull
    private List<Result> parseResults(@NonNull JSONObject json) throws Exception {
        List<Result> results = new ArrayList<>();
        String status = json.optString("status");
        if (!"OK".equals(status) && !"ZERO_RESULTS".equals(status)) {
            throw new IllegalStateException("Google Geocoding status: " + status);
        }
        JSONArray array = json.optJSONArray("results");
        if (array == null) return results;
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            JSONObject geometry = item.optJSONObject("geometry");
            JSONObject location = geometry == null ? null : geometry.optJSONObject("location");
            if (location == null) continue;
            Result result = new Result();
            result.name = item.optString("formatted_address", "");
            result.address = result.name;
            result.latitude = location.optDouble("lat");
            result.longitude = location.optDouble("lng");
            result.placeId = item.optString("place_id", null);
            results.add(result);
        }
        return results;
    }
}
