package net.osmand.plus.plugins.googlemaps;

import androidx.annotation.NonNull;

import net.osmand.data.LatLon;
import net.osmand.search.core.ObjectType;
import net.osmand.search.core.SearchCoreFactory;
import net.osmand.search.core.SearchPhrase;
import net.osmand.search.core.SearchResult;
import net.osmand.search.SearchUICore.SearchResultMatcher;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** SearchUICore provider backed by Google's Geocoding API. */
public final class GoogleAddressSearchAPI extends SearchCoreFactory.SearchBaseAPI {
    private static final int PRIORITY = 510;
    private final GoogleMapsPlugin plugin;

    public GoogleAddressSearchAPI(@NonNull GoogleMapsPlugin plugin) {
        super(ObjectType.LOCATION, ObjectType.HOUSE, ObjectType.STREET, ObjectType.CITY, ObjectType.VILLAGE);
        this.plugin = plugin;
    }

    @Override
    public int getSearchPriority(SearchPhrase phrase) {
        if (!plugin.googleSearchEnabled() || plugin.API_KEY.get().trim().isEmpty()) {
            return -1;
        }
        if (phrase.isEmpty()) {
            return -1;
        }
        return plugin.SEARCH_MODE.get() == GoogleMapsPlugin.SearchMode.GOOGLE_ONLY ? 1000 : PRIORITY;
    }

    @Override
    public boolean isSearchMoreAvailable(SearchPhrase phrase) {
        return false;
    }

    @Override
    public boolean search(SearchPhrase phrase, SearchResultMatcher resultMatcher) throws IOException {
        String query = phrase.getFullSearchPhrase();
        if (query == null || query.trim().isEmpty()) {
            return false;
        }

        try {
            String encodedQuery = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8.name());
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodedQuery
                    + "&key=" + URLEncoder.encode(plugin.API_KEY.get().trim(), StandardCharsets.UTF_8.name());
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            try {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return false;
                }
                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                JSONObject json = new JSONObject(response.toString());
                if (!"OK".equals(json.optString("status"))) {
                    return false;
                }
                JSONArray results = json.optJSONArray("results");
                if (results == null) {
                    return false;
                }
                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    JSONObject geometry = result.optJSONObject("geometry");
                    JSONObject location = geometry == null ? null : geometry.optJSONObject("location");
                    if (location == null) {
                        continue;
                    }
                    double lat = location.optDouble("lat", Double.NaN);
                    double lon = location.optDouble("lng", Double.NaN);
                    if (Double.isNaN(lat) || Double.isNaN(lon)) {
                        continue;
                    }
                    String formatted = result.optString("formatted_address", query);
                    String placeId = result.optString("place_id", "");

                    SearchResult sr = new SearchResult(phrase);
                    sr.localeName = formatted;
                    sr.addressName = formatted;
                    sr.location = new LatLon(lat, lon);
                    sr.objectType = ObjectType.LOCATION;
                    sr.object = new GoogleAddressResult(formatted, placeId, lat, lon);
                    sr.priority = PRIORITY;
                    sr.preferredZoom = SearchCoreFactory.PREFERRED_DEFAULT_ZOOM;
                    resultMatcher.publish(sr);
                }
                return true;
            } finally {
                connection.disconnect();
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    public static final class GoogleAddressResult {
        public final String formattedAddress;
        public final String placeId;
        public final double latitude;
        public final double longitude;

        public GoogleAddressResult(String formattedAddress, String placeId, double latitude, double longitude) {
            this.formattedAddress = formattedAddress;
            this.placeId = placeId;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
