# Source inventory

This inventory records the custom-change files retained in `999nowa/b/inympningar` after consolidation of the previous `999nowa/OsmAnd` work.

## Retained: feature/google-maps-addon current transplantation files

These files remain because they represent the current transplantation material that is still relevant to the integration:

- `OsmAnd/res/values/google_maps_arrays.xml`
- `OsmAnd/res/xml/google_maps_settings.xml`
- `OsmAnd/src/net/osmand/plus/googlemaps/GoogleAddressSearchClient.java`
- `OsmAnd/src/net/osmand/plus/googlemaps/GoogleMapsPreferences.java`
- `OsmAnd/src/net/osmand/plus/plugins/googlemaps/GoogleAddressSearchAPI.java`
- `OsmAnd/src/net/osmand/plus/plugins/googlemaps/GoogleMapsPlugin.java`
- `OsmAnd/src/net/osmand/plus/plugins/googlemaps/GoogleMapsSettingsFragment.java`

## Retained: osmand-5.3.10-google-tiles build implementation

These files are retained because they belong to the 5.3.10 Google Satellite + Google Search implementation and its build path, including files used by the known build workflow:

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

## Removed as superseded

The following staging material was removed because it was an earlier standalone prototype or a historical duplicate that is not part of the retained 5.3.10 build implementation:

- `inympningar/google-maps-addon/`
- `inympningar/historical-satellite/`

The original source variants remain recoverable from the Git history of `999nowa/OsmAnd`.

## Migration status

The staging area now contains the retained current transplantation material and the build-used 5.3.10 implementation rather than every historical prototype. The old `999nowa/OsmAnd` repository is being retired by moving its branch heads to empty-tree commits. Its existing Git commits are not deleted, so the historical source remains recoverable by commit SHA.
