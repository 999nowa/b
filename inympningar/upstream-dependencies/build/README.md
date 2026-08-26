# OsmAnd Build repository dependency

The OsmAnd Core Legacy configure scripts depend on the separate upstream repository:

https://github.com/osmandapp/OsmAnd-build

Required path:

`build/utils/functions.sh`

The upstream build repository was cloned during the local build investigation because Core Legacy's GDAL, PROJ and SQLite configure scripts source this file.

This repository does not duplicate the complete OsmAnd-build project. Use the upstream repository at its required location when reconstructing the build workspace.

## Workspace layout

```text
workspace/
├── OsmAnd-feature-google-maps-satellite/
├── core-legacy/
└── build/
    └── utils/
        └── functions.sh
```

The Core Legacy scripts expect `core-legacy/externals/.../../../../build/utils/functions.sh` to resolve to the sibling `build/utils/functions.sh` location.
