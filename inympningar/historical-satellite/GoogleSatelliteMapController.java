package net.osmand.plus.googlemaps;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osmand.util.Algorithms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Experimental bridge for a Google Maps satellite map.
 * The Google Maps API key is entered by the user at runtime and stored locally.
 */
public final class GoogleSatelliteMapController {

    private static final String MAP_VIEW_CLASS = "com.google.android.gms.maps.MapView";
    private static final String MAP_OPTIONS_CLASS = "com.google.android.gms.maps.GoogleMapOptions";
    private static final String MAP_TYPE_SATELLITE = "MAP_TYPE_SATELLITE";

    private GoogleSatelliteMapController() {
    }

    @Nullable
    public static View createMapView(@NonNull Context context) {
        if (Algorithms.isEmpty(GoogleMapsPreferences.getApiKey(context))) {
            GoogleMapsPreferences.showApiKeyDialog(context);
            return null;
        }
        try {
            Class<?> optionsClass = Class.forName(MAP_OPTIONS_CLASS);
            Object options = optionsClass.getDeclaredConstructor().newInstance();
            Method mapType = optionsClass.getMethod("mapType", int.class);
            mapType.invoke(options, resolveSatelliteType());

            Class<?> mapViewClass = Class.forName(MAP_VIEW_CLASS);
            Constructor<?> constructor = mapViewClass.getConstructor(Context.class, optionsClass);
            return (View) constructor.newInstance(context, options);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static boolean attachSatelliteMap(@NonNull FrameLayout container) {
        View mapView = createMapView(container.getContext());
        if (mapView == null) {
            return false;
        }
        container.removeAllViews();
        container.addView(mapView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        return true;
    }

    private static int resolveSatelliteType() throws Exception {
        Class<?> googleMapClass = Class.forName("com.google.android.gms.maps.GoogleMap");
        return googleMapClass.getField(MAP_TYPE_SATELLITE).getInt(null);
    }
}
