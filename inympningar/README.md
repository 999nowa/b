# Inympningar

## Senaste ändring

### 2026-08-26: Bevara relevanta upstream-beroenden från `utgick`

**Vad ändrades:** De tre unika referensdokumenten från `999nowa/utgick` som bedömdes relevanta för framtida rekonstruktion av bygget har bevarats under `inympningar/upstream-dependencies/`.

**Varför:** `core-legacy/CMakeLists-GDAL-upstream.txt`, `core-legacy/README.md` och `build/README.md` innehåller inte den äldre Google-applikationsimplementation som bör ympas in. De dokumenterar däremot upstream Core Legacy/GDAL och beroendet till `OsmAnd-build`.

**Påverkan:** Vi behåller relevant teknisk historik och byggproveniens utan att återinföra föråldrad Google-kod. Inga befintliga aktiva projektfiler har skrivits över och ingen build eller GitHub Actions-workflow har startats.

## Source

Primary source branch: `feature/google-maps-addon` in `999nowa/OsmAnd`.

Additional historical branches remain untouched in the source repository and are not deleted.

## What is preserved here

The imported material covers our custom Google Maps integration, Google address search, search-provider selection, API-key preferences, and Google map-tile support.

Relevant upstream dependency references are preserved separately under `upstream-dependencies/`.

## Why

`999nowa/b` is the main development repository, while `999nowa/OsmAnd` remains the historical source until migration and build verification are complete.

## Important

These files are staged under `inympningar/` and are not yet merged into the active OsmAnd source tree. This avoids overwriting the existing project while the transplantation is being verified.

The original source files and Git history remain in `999nowa/OsmAnd`.
