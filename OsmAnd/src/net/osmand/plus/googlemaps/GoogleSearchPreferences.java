package net.osmand.plus.googlemaps;

import android.content.Context;
import androidx.annotation.NonNull;

public final class GoogleSearchPreferences {
    private static final String PREFS = "google_maps_integration";
    private static final String USE_GOOGLE_SEARCH = "use_google_search";
    private GoogleSearchPreferences() {}

    public static boolean isGoogleSearchEnabled(@NonNull Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(USE_GOOGLE_SEARCH, true);
    }

    public static void setGoogleSearchEnabled(@NonNull Context context, boolean enabled) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(USE_GOOGLE_SEARCH, enabled).apply();
    }
}
