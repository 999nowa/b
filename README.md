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

## Change log

### 2026-08-26: Consolidate transplantation material and retire the old OsmAnd working repository

**What changed:** The `inympningar/` staging area was reviewed against the custom branches of `999nowa/OsmAnd`. The current transplantation material and the files belonging to the `osmand-5.3.10-google-tiles` build path are retained. Superseded standalone Google Maps prototype material and the historical satellite-only duplicate are being removed from `inympningar`.

**Why:** The objective is to make `999nowa/b` the main development repository without carrying forward obsolete implementations that were later replaced. Files that belong to the known 5.3.10 Google Satellite + Google Search build path are retained because they form part of the buildable implementation rather than merely an earlier prototype.

**Impact:** `999nowa/b` remains the active development location. The old `999nowa/OsmAnd` repository is being emptied at its branch heads only after the relevant current and build-used files have been preserved here. Its Git history is not rewritten or deleted, so the old commits remain recoverable as historical records.

### 2026-08-26: OsmAnd custom changes staged for transplantation

**What changed:** A new `inympningar/` area was added containing verified files from `999nowa/OsmAnd` branches that contain our custom Google Maps, address-search, satellite and related integration work.

**Why:** `999nowa/b` is becoming the main OsmAnd development repository, while the previous `999nowa/OsmAnd` repository must remain untouched until the migration has been verified.

**Impact:** The imported files are preserved as transplantation material under `inympningar/`. Existing project files are not replaced by this import, and the original `999nowa/OsmAnd` repository remains available as the source and historical backup.
