package net.osmand.plus.googlemaps;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

/** Local preferences for the optional Google Maps integration. */
public final class GoogleMapsPreferences {
    private static final String PREFS = "google_maps_integration";
    private static final String API_KEY = "api_key";
    private static final String USE_GOOGLE_SEARCH = "use_google_search";
    private static final String USE_OSMAND_SEARCH = "use_osmand_search";
    private static final String GOOGLE_FIRST = "google_first";
    private GoogleMapsPreferences() {}
    private static SharedPreferences prefs(@NonNull Context context) { return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE); }
    public static String getApiKey(@NonNull Context context) { return prefs(context).getString(API_KEY, ""); }
    public static void setApiKey(@NonNull Context context, String value) { prefs(context).edit().putString(API_KEY, value == null ? "" : value.trim()).apply(); }
    public static boolean useGoogleSearch(@NonNull Context context) { return prefs(context).getBoolean(USE_GOOGLE_SEARCH, false); }
    public static void setUseGoogleSearch(@NonNull Context context, boolean enabled) { prefs(context).edit().putBoolean(USE_GOOGLE_SEARCH, enabled).apply(); }
    public static boolean useOsmandSearch(@NonNull Context context) { return prefs(context).getBoolean(USE_OSMAND_SEARCH, true); }
    public static void setUseOsmandSearch(@NonNull Context context, boolean enabled) { prefs(context).edit().putBoolean(USE_OSMAND_SEARCH, enabled).apply(); }
    public static boolean googleFirst(@NonNull Context context) { return prefs(context).getBoolean(GOOGLE_FIRST, false); }
    public static void setGoogleFirst(@NonNull Context context, boolean enabled) { prefs(context).edit().putBoolean(GOOGLE_FIRST, enabled).apply(); }
}
