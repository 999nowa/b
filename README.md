# OsmAnd integration build bundle

This repository is the buildable integration bundle derived from `999nowa/utgick`.

## What is included

The `OsmAnd/` directory contains the modified application files from `utgick`, including:

- optional Google Geocoding client
- local Google Maps preferences
- experimental Google Satellite `MapView` bridge
- Google search provider mode model

The `docs/` directory contains the integration documentation.

## Build procedure

GitHub Actions clones the `feature/google-maps-satellite` branch of `999nowa/OsmAnd`, clones `OsmAnd-resources` and `OsmAnd-build`, copies this repository's integration files into the corresponding OsmAnd paths, and builds a debug APK with Gradle.

The resulting APK is uploaded as the `lugnt-debug-apk` Actions artifact. If the APK is below GitHub's normal 100 MB repository file limit, the workflow also commits it as `app/build/outputs/apk/debug/lugnt-debug.apk`.

## Important limitation

The files from `utgick` are an integration bundle, not a complete OsmAnd fork. The Google Satellite bridge deliberately uses reflection and therefore does not by itself add a visible Satellite button or a Google Maps API key. The Google Maps SDK dependency, API key configuration, MapView lifecycle, UI insertion, and camera synchronisation still require the corresponding OsmAnd integration work. No API key is stored in this repository.
