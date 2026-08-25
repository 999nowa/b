package net.osmand.plus.googlemaps;

import android.content.Context;
import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class GooglePlacesSearchProvider {
    private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/place/textsearch/json";
    public static final class Result {
        public final String name, address, placeId;
        public final double latitude, longitude;
        public Result(String name, String address, double latitude, double longitude, String placeId) {
            this.name = name; this.address = address; this.latitude = latitude; this.longitude = longitude; this.placeId = placeId;
        }
    }
    private GooglePlacesSearchProvider() {}

    @NonNull
    public static List<Result> search(@NonNull Context context, @NonNull String query) throws Exception {
        String key = GoogleMapsPreferences.getApiKey(context);
        if (key.isEmpty()) throw new IllegalStateException("Google Maps API key is not configured");
        String url = ENDPOINT + "?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8.name())
                + "&language=sv&key=" + URLEncoder.encode(key, StandardCharsets.UTF_8.name());
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET"); connection.setConnectTimeout(15000); connection.setReadTimeout(15000);
        int status = connection.getResponseCode();
        InputStream stream = status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream();
        String response = read(stream); connection.disconnect();
        if (status < 200 || status >= 300) throw new IllegalStateException("Google Places HTTP " + status);
        JSONObject root = new JSONObject(response);
        String apiStatus = root.optString("status", "");
        if (!("OK".equals(apiStatus) || "ZERO_RESULTS".equals(apiStatus))) throw new IllegalStateException("Google Places API: " + apiStatus);
        JSONArray results = root.optJSONArray("results"); List<Result> output = new ArrayList<>();
        if (results == null) return output;
        for (int i = 0; i < results.length(); i++) {
            JSONObject item = results.getJSONObject(i); JSONObject geometry = item.optJSONObject("geometry");
            JSONObject location = geometry == null ? null : geometry.optJSONObject("location");
            if (location == null) continue;
            output.add(new Result(item.optString("name", ""), item.optString("formatted_address", ""),
                    location.optDouble("lat"), location.optDouble("lng"), item.optString("place_id", "")));
        }
        return output;
    }
    private static String read(InputStream stream) throws Exception {
        if (stream == null) return ""; StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line; while ((line = reader.readLine()) != null) result.append(line);
        }
        return result.toString();
    }
}
