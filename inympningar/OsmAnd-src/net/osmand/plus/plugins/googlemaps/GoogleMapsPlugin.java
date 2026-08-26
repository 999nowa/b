package net.osmand.plus.plugins.googlemaps;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.plugins.OsmandPlugin;
import net.osmand.plus.settings.backend.preferences.CommonPreference;
import net.osmand.plus.settings.fragments.SettingsScreenType;

/**
 * Host-side adapter for the isolated Google Maps add-on.
 *
 * The feature implementation remains in google-maps-addon/android. This
 * class deliberately exposes only OsmAnd plugin preferences and lifecycle
 * hooks, so future OsmAnd updates have a small integration surface.
 */
public class GoogleMapsPlugin extends OsmandPlugin {
    public static final String PLUGIN_ID = "google_maps_addon";

    public enum SearchMode {
        OSMAND_ONLY,
        GOOGLE_ONLY,
        BOTH
    }

    public enum MapMode {
        OSMAND,
        GOOGLE_SATELLITE,
        GOOGLE_HYBRID
    }

    public final CommonPreference<String> API_KEY;
    public final CommonPreference<SearchMode> SEARCH_MODE;
    public final CommonPreference<MapMode> MAP_MODE;

    public GoogleMapsPlugin(@NonNull OsmandApplication app) {
        super(app);
        API_KEY = registerStringPreference("google_maps_api_key", "").makeGlobal();
        SEARCH_MODE = registerEnumStringPreference("google_maps_search_mode", SearchMode.OSMAND_ONLY,
                SearchMode.values(), SearchMode.class).makeGlobal();
        MAP_MODE = registerEnumStringPreference("google_maps_map_mode", MapMode.OSMAND,
                MapMode.values(), MapMode.class).makeGlobal();
    }

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    @Override
    public String getName() {
        return "Google Maps";
    }

    @Override
    public CharSequence getDescription(boolean linksEnabled) {
        return "Google satellite imagery and address search integration";
    }

    @Override
    public int getLogoResourceId() {
        return R.drawable.ic_world_globe_dark;
    }

    @Nullable
    @Override
    public Drawable getAssetResourceImage() {
        return app.getUIUtilities().getIcon(R.drawable.online_maps);
    }

    @Nullable
    @Override
    public SettingsScreenType getSettingsScreenType() {
        return SettingsScreenType.GOOGLE_MAPS_SETTINGS;
    }

    @Override
    public boolean init(@NonNull OsmandApplication app, @Nullable Activity activity) {
        return super.init(app, activity);
    }

    public boolean googleSearchEnabled() {
        return SEARCH_MODE.get() == SearchMode.GOOGLE_ONLY || SEARCH_MODE.get() == SearchMode.BOTH;
    }

    public boolean googleMapEnabled() {
        return MAP_MODE.get() != MapMode.OSMAND;
    }
}
