# OsmAnd~ 5.3.10 Google integration build bundle

This repository contains only our integration files and build automation. The complete OsmAnd application source is fetched from the upstream `osmandapp/OsmAnd` `r5.3` branch during GitHub Actions and is not stored in this repository.

The integration includes the local Google Maps API key storage, Google Places address search bridge, and Google search provider registration. GitHub Actions uses JDK 17, applies the files to the upstream source, and builds the APK.

No Google API key is stored in this repository.
