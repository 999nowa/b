# Experimental Google Satellite map

This branch is an initial experiment for replacing the visible OsmAnd map surface with a Google Satellite map while retaining the OsmAnd application and navigation stack.

## Required integration steps

1. Add `com.google.android.gms:play-services-maps` to the Android application dependencies.
2. Add Google Maps API key metadata to the application manifest through protected/local configuration. Never commit the key.
3. Insert the experimental `MapView` into the appropriate OsmAnd map UI as an alternative rendering surface.
4. Forward the Google `MapView` lifecycle methods.
5. Synchronize the Google camera with OsmAnd's current map center and zoom.
6. Keep OsmAnd's existing renderer available as a fallback until overlays and projection are verified.

The intended architecture is to make Google Satellite a selectable rendering backend rather than replacing OsmAnd's routing or navigation logic.

## Build environment

`OsmAnd-core-legacy` external configure scripts expect OsmAnd's build utilities. The build workflow clones OsmAnd-build alongside the source tree.
