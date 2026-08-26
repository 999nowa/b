package net.osmand.googlemaps;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

/**
 * Isolated Google Maps view factory. OsmAnd integration code should depend on
 * this small surface instead of importing Google Maps classes throughout the app.
 */
public final class GoogleMapsFeature {
    public enum MapType {
        SATELLITE,
        HYBRID
    }

    private GoogleMapsFeature() {
    }

    @Nullable
    public static MapView createMapView(@NonNull Context context, @NonNull MapType type) {
        int mapType = type == MapType.HYBRID
                ? GoogleMap.MAP_TYPE_HYBRID
                : GoogleMap.MAP_TYPE_SATELLITE;
        GoogleMapOptions options = new GoogleMapOptions().mapType(mapType);
        return new MapView(context, options);
    }

    public static boolean attach(@NonNull FrameLayout container, @NonNull MapType type) {
        MapView mapView = createMapView(container.getContext(), type);
        if (mapView == null) {
            return false;
        }
        container.removeAllViews();
        container.addView(mapView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        mapView.onCreate(null);
        mapView.onResume();
        return true;
    }

    public static void pause(@Nullable View view) {
        if (view instanceof MapView) {
            ((MapView) view).onPause();
        }
    }

    public static void resume(@Nullable View view) {
        if (view instanceof MapView) {
            ((MapView) view).onResume();
        }
    }
}
