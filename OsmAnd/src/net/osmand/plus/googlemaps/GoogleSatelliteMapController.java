package net.osmand.plus.googlemaps;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Google Maps Tile API satellite session and tile URL helper.
 * This deliberately does not depend on the Google Maps Android SDK.
 */
public final class GoogleSatelliteMapController {
    private static final String SESSION_ENDPOINT = "https://tile.googleapis.com/v1/createSession";
    private static final String TILE_ENDPOINT = "https://tile.googleapis.com/v1/2dtiles/";

    private GoogleSatelliteMapController() {}

    @Nullable
    public static String createSatelliteSession(@NonNull Context context) {
        String key = GoogleMapsPreferences.getApiKey(context);
        if (key.trim().isEmpty()) {
            return null;
        }
        HttpURLConnection connection = null;
        try {
            URL url = new URL(SESSION_ENDPOINT + "?key=" + java.net.URLEncoder.encode(key, "UTF-8"));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            JSONObject request = new JSONObject();
            request.put("mapType", "satellite");
            request.put("language", "sv-SE");
            request.put("region", "SE");
            try (OutputStream output = connection.getOutputStream()) {
                output.write(request.toString().getBytes(StandardCharsets.UTF_8));
            }

            int status = connection.getResponseCode();
            InputStream stream = status >= 200 && status < 300
                    ? connection.getInputStream() : connection.getErrorStream();
            String response = read(stream);
            if (status < 200 || status >= 300) {
                return null;
            }
            return new JSONObject(response).optString("session", null);
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @NonNull
    public static String tileUrl(@NonNull Context context, @NonNull String session, int zoom, int x, int y) {
        String key = GoogleMapsPreferences.getApiKey(context);
        return TILE_ENDPOINT + zoom + "/" + x + "/" + y
                + "?session=" + encode(session) + "&key=" + encode(key);
    }

    private static String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
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
