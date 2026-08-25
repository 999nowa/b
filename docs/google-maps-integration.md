# Google Maps integration

The Google integration is optional and must not replace OsmAnd search unless the user chooses it.

## Search modes

- OsmAnd only: `useOsmandSearch=true`, `useGoogleSearch=false`
- Google only: `useOsmandSearch=false`, `useGoogleSearch=true`
- Both: both flags enabled

When both are enabled, the UI should expose a priority selector. `googleFirst=false` means OsmAnd results are presented first; `googleFirst=true` means Google results are presented first.

## API key

The Google API key is entered by the user in the app and stored locally through `GoogleMapsPreferences`. It must never be committed to the repository, placed in source code, or hard-coded in the manifest.

## Current implementation

`GoogleMapsPreferences` provides persistent local settings.

`GoogleAddressSearchClient` provides the optional Google Geocoding API request and converts responses to a provider-neutral result object containing address, coordinates and place ID.

The remaining UI integration should expose these preferences under the application's Google Maps settings and register the client as an additional search provider when Google search is enabled.
