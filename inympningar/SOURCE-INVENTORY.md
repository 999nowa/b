# Source inventory

This inventory records the custom-change files identified in `999nowa/OsmAnd` before transplantation.

## feature/google-maps-addon

- `.github/workflows/google-maps-addon-build.yml`
- `.github/workflows/google-maps-addon.yml`
- `OsmAnd/res/values/google_maps_arrays.xml`
- `OsmAnd/res/xml/google_maps_settings.xml`
- `OsmAnd/src/net/osmand/plus/googlemaps/GoogleAddressSearchClient.java`
- `OsmAnd/src/net/osmand/plus/googlemaps/GoogleMapsPreferences.java`
- `OsmAnd/src/net/osmand/plus/plugins/googlemaps/GoogleAddressSearchAPI.java`
- `OsmAnd/src/net/osmand/plus/plugins/googlemaps/GoogleMapsPlugin.java`
- `OsmAnd/src/net/osmand/plus/plugins/googlemaps/GoogleMapsSettingsFragment.java`
- `docs/GOOGLE_SATELLITE_EXPERIMENT.md`
- `docs/google-maps-integration.md`
- `features/google-maps/README.md`
- `features/google-maps/apply-google-maps.sh`
- `features/google-maps/apply-to-osmand.sh`
- `features/google-maps/build.gradle`
- `features/google-maps/patches/OsmAnd-build.gradle.patch`
- `features/google-maps/patches/settings.gradle.patch`
- `features/google-maps/remove-google-maps.sh`
- `features/google-maps/src/main/AndroidManifest.xml`
- `features/google-maps/src/main/java/net/osmand/googlemaps/GoogleMapsFeature.java`
- `features/google-maps/src/main/java/net/osmand/googlemaps/search/GoogleAddressSearchClient.java`
- `features/google-maps/src/main/java/net/osmand/googlemaps/search/SearchProviderMode.java`
- `features/google-maps/src/main/java/net/osmand/googlemaps/settings/GoogleMapsPreferences.java`
- `features/google-maps/update-and-apply.sh`
- `google-maps-addon/COMPATIBILITY.md`
- `google-maps-addon/INSTALL.md`
- `google-maps-addon/README.md`
- `google-maps-addon/USER-GUIDE.md`
- `google-maps-addon/android/GOOGLE_MAPS_SDK.md`
- `google-maps-addon/android/README.md`
- `google-maps-addon/android/build.gradle`
- `google-maps-addon/android/settings.gradle`
- `google-maps-addon/android/src/main/AndroidManifest.xml`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/GoogleGeocodingClient.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/GoogleMapHostController.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/GoogleMapLayerPolicy.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/GoogleMapOverlayBridge.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/GoogleMapSurfaceAdapter.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/GoogleMapSurfaceController.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/GoogleMapTilesSession.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/GoogleMapViewContract.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/GoogleMapsAddonConfig.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/GoogleMapsHost.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/GoogleMapsSdkSurfaceAdapter.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/GoogleMapsTilePolicy.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/MapSource.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/OsmAndCameraSource.java`
- `google-maps-addon/android/src/main/java/net/osmand/googlemaps/addon/SearchProviderMode.java`
- `google-maps-addon/patcher/README.md`
- `google-maps-addon/patches/0001-register-google-maps-plugin.patch`
- `google-maps-addon/patches/0003-add-google-maps-settings-screen.patch`
- `google-maps-addon/patches/0004-integrate-google-search-with-searchuicore.patch`

## feature/google-maps-satellite

Additional files unique to that branch:

- `OsmAnd/src/net/osmand/plus/googlemaps/GoogleSatelliteMapController.java`
- `OsmAnd/src/net/osmand/plus/search/GoogleSearchSettings.java`

The branch also contains its own versions of `GoogleAddressSearchClient.java`, `GoogleMapsPreferences.java`, `GOOGLE_SATELLITE_EXPERIMENT.md`, and `google-maps-integration.md`. Those versions are retained in the original repository as historical material because they conflict by path with the consolidated add-on implementation.

## osmand-5.3.10-google-tiles

- `.github/workflows/build-osmand-google-tiles.yml`
- `OsmAnd-java/src/main/java/net/osmand/binary/CommonWords.java` (modified)
- `OsmAnd/AndroidManifest-androidFull.xml` (modified)
- `OsmAnd/src-google/net/osmand/plus/googlemaps/GoogleMapTilesSession.java`
- `OsmAnd/src-google/net/osmand/plus/googlemaps/GoogleMapsPreferences.java`
- `OsmAnd/src-google/net/osmand/plus/googlemaps/GoogleMapsSettingsActivity.java`
- `OsmAnd/src-google/net/osmand/plus/googlemaps/GooglePlacesSearchProvider.java`
- `OsmAnd/src-google/net/osmand/plus/googlemaps/GoogleSearchApi.java`
- `OsmAnd/src-google/net/osmand/plus/googlemaps/GoogleSearchPreferences.java`
- `OsmAnd/src-google/net/osmand/plus/googlemaps/GoogleSearchSource.java`
- `tools/enable_google_search.py`

## Staging status

Core source files have been copied under `inympningar/` without replacing the active `OsmAnd/` tree in `999nowa/b`. Historical/conflicting variants are intentionally not overwritten or silently merged.
