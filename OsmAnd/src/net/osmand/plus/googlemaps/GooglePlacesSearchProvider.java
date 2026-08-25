package net.osmand.plus.googlemaps;

import android.content.Context;
import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Google Places API (New) Text Search client. */
public final class GooglePlacesSearchProvider {
    private static final String ENDPOINT = "https://places.googleapis.com/v1/places:searchText";

    public static final class Result {
        public final String name;
        public final String address;
        public final String placeId;
        public final double latitude;
        public final double longitude;

        public Result(String name, String address, double latitude, double longitude, String placeId) {
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.placeId = placeId;
        }
    }

    private GooglePlacesSearchProvider() {}

    @NonNull
    public static List<Result> search(@NonNull Context context, @NonNull String query) throws Exception {
        String key = GoogleMapsPreferences.getApiKey(context);
        if (key.trim().isEmpty()) {
            throw new IllegalStateException("Google Maps API key is not configured");
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("X-Goog-Api-Key", key);
        connection.setRequestProperty("X-Goog-FieldMask",
                "places.id,places.displayName,places.formattedAddress,places.location");

        JSONObject request = new JSONObject();
        request.put("textQuery", query);
        request.put("languageCode", "sv");
        try (OutputStream output = connection.getOutputStream()) {
            output.write(request.toString().getBytes(StandardCharsets.UTF_8));
        }

        int status = connection.getResponseCode();
        InputStream stream = status >= 200 && status < 300
                ? connection.getInputStream() : connection.getErrorStream();
        String response = read(stream);
        connection.disconnect();

        if (status < 200 || status >= 300) {
            throw new IllegalStateException("Google Places HTTP " + status);
        }

        JSONObject root = new JSONObject(response);
        JSONArray results = root.optJSONArray("places");
        List<Result> output = new ArrayList<>();
        if (results == null) {
            return output;
        }

        for (int i = 0; i < results.length(); i++) {
            JSONObject item = results.getJSONObject(i);
            JSONObject displayName = item.optJSONObject("displayName");
            JSONObject location = item.optJSONObject("location");
            if (location == null) {
                continue;
            }
            output.add(new Result(
                    displayName == null ? "" : displayName.optString("text", ""),
                    item.optString("formattedAddress", ""),
                    location.optDouble("latitude"),
                    location.optDouble("longitude"),
                    item.optString("id", "")
            ));
        }
        return output;
    }

    private static String read(InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }
}
