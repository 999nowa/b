# OsmAnd Nightly Google integration

This repository contains the integration layer and GitHub Actions build automation. The complete OsmAnd source is fetched from the upstream `osmandapp/OsmAnd` `master` branch during the build and the `nightlyFree` flavor is compiled. This is how the upstream project defines its Nightly build.

The integration provides:

- a Google Maps API key field in OsmAnd Settings > Other > Google Maps
- an explicit `Use Google for address search` switch, disabled by default
- a Google Places address-search bridge that publishes results as normal OsmAnd `SearchResult` objects
- experimental Google satellite integration code
- JDK 17 GitHub Actions configuration

No Google API key is stored in this repository. The key is entered locally in the installed app.

The workflow records the exact upstream OsmAnd, resources, build-tools and core-legacy commits used for each build.
