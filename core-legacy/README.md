# OsmAnd Core Legacy integration

This directory documents the OsmAnd Core Legacy component required by the Google satellite experiment.

## Upstream source

Repository: https://github.com/osmandapp/OsmAnd-core-legacy
GDAL integration commit: `befa0be3b973be5b44246dd63ec0871a1ced8411`
Parent commit: `ef9a76130eff2f139393913cf36b078cd3d4e6bd`

The GDAL integration is an upstream OsmAnd Core Legacy change, not a local modification made specifically in this experiment. It adds GeoTIFF/heightmap processing and the GDAL, PROJ and SQLite external dependencies.

## Files changed by the upstream GDAL commit

- `CMakeLists.txt`
- `externals/gdal/configure.sh`
- `externals/gdal/gdal_version.h`
- `externals/gdal/patches/0-replace-uint.patch`
- `externals/gdal/stamp`
- `externals/proj/configure.sh`
- `externals/proj/stamp`
- `externals/sqlite/configure.sh`
- `externals/sqlite/stamp`
- `native/src/heightmapRenderer.cpp`
- `native/src/heightmapRenderer.h`
- `native/src/java_wrap.cpp`
- `targets/.cmake/CMakeLists.txt`
- `targets/.cmake/projects/OsmAndCore/CMakeLists.txt`
- `targets/.cmake/projects/gdal/CMakeLists.txt`
- `targets/.cmake/projects/gdal/cpl_config.cmake`
- `targets/.cmake/projects/gdal/cpl_config.h.in`
- `targets/.cmake/projects/proj/CMakeLists.txt`
- `targets/.cmake/projects/proj/proj_config.cmake`
- `targets/.cmake/projects/proj/proj_config.h.in`
- `targets/.cmake/projects/sqlite/CMakeLists.txt`
- `externals/zlib/CMakeLists.txt`

## Important build dependency

The configure scripts above source:
`build/utils/functions.sh`

That file belongs to the separate upstream repository `osmandapp/OsmAnd-build`. It must be available as a sibling directory when building Core Legacy, for example:

`<workspace>/build/utils/functions.sh`

The local build error that showed `.../build/utils/functions.sh: No such file or directory` was therefore a workspace layout/dependency problem, not evidence of a missing Google satellite source file.

## What is actually custom in this project

The custom Google integration is under `OsmAnd/` in the root of this repository. Core Legacy is recorded here as an upstream dependency/reference so the complete integration can be reconstructed without confusing upstream code with custom application changes.
