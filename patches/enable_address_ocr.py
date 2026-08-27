#!/usr/bin/env python3
from pathlib import Path
import re
import shutil
import sys

ROOT = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path.cwd()
APP = ROOT / "OsmAnd"
SRC = APP / "src" / "net" / "osmand" / "plus"
INTEGRATION = ROOT.parent / "b" if False else Path(__file__).resolve().parents[1]


def fail(message):
    raise SystemExit(message)


def copy_required(relative):
    source = INTEGRATION / relative
    destination = ROOT / relative
    if not source.is_file():
        fail(f"Missing integration file: {source}")
    destination.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(source, destination)


# The workflow runs this script from the integration repository root, so the
# original b checkout is ROOT. Keep the paths explicit and deterministic.
for relative in [
    "OsmAnd/src/net/osmand/plus/addressocr/AddressOcrRouteActivity.java",
    "OsmAnd/src/net/osmand/plus/addressocr/AddressOcrRouteLauncher.java",
]:
    copy_required(relative)

# Add ML Kit only once.
gradle = APP / "build.gradle"
text = gradle.read_text(encoding="utf-8")
dep = "\timplementation 'com.google.mlkit:text-recognition:16.0.1'"
if dep not in text:
    marker = "dependencies {\n"
    if marker not in text:
        fail("Could not find dependencies block in OsmAnd/build.gradle")
    text = text.replace(marker, marker + dep + "\n", 1)
    gradle.write_text(text, encoding="utf-8")

# Register scanner activity in the main manifest.
manifest = APP / "AndroidManifest.xml"
m = manifest.read_text(encoding="utf-8")
activity_decl = '    <activity android:name=".addressocr.AddressOcrRouteActivity" android:exported="false" />'
if activity_decl not in m:
    marker = "<application"
    pos = m.find(marker)
    if pos < 0:
        fail("Could not find application element in AndroidManifest.xml")
    app_close = m.find(">", pos)
    if app_close < 0:
        fail("Could not find opening application tag")
    m = m[:app_close + 1] + "\n" + activity_decl + m[app_close + 1:]
    manifest.write_text(m, encoding="utf-8")

# Add the OCR launcher to MapActivity exactly once.
map_activity = APP / "src" / "net" / "osmand" / "plus" / "activities" / "MapActivity.java"
s = map_activity.read_text(encoding="utf-8")
imp = "import net.osmand.plus.addressocr.AddressOcrRouteLauncher;"
if imp not in s:
    anchor = "import net.osmand.plus.AppInitializeListener;"
    if anchor not in s:
        fail("Could not find MapActivity import anchor")
    s = s.replace(anchor, anchor + "\n" + imp, 1)

if "AddressOcrRouteLauncher.install(this);" not in s:
    class_pos = s.find("public class MapActivity")
    if class_pos < 0:
        fail("Could not locate MapActivity class declaration")
    match = re.search(r"protected\s+void\s+onCreate\s*\(\s*@Nullable\s+Bundle\s+savedInstanceState\s*\)", s[class_pos:])
    if not match:
        match = re.search(r"(?:protected|public)\s+void\s+onCreate\s*\(\s*[^)]*Bundle\s+savedInstanceState\s*\)", s[class_pos:])
    if not match:
        fail("Could not locate MapActivity.onCreate")
    method_start = class_pos + match.start()
    super_pos = s.find("super.onCreate(savedInstanceState);", method_start)
    if super_pos < 0:
        fail("Could not find MapActivity super.onCreate")
    insert_at = super_pos + len("super.onCreate(savedInstanceState);")
    s = s[:insert_at] + "\n\t\tAddressOcrRouteLauncher.install(this);" + s[insert_at:]
    map_activity.write_text(s, encoding="utf-8")

print("Address OCR route integration applied")
