from pathlib import Path
import re
import shutil
import sys

if len(sys.argv) != 2:
    raise SystemExit("usage: enable_google_search.py <OsmAnd checkout>")

repo = Path(sys.argv[1]).resolve()
plus = repo / 'OsmAnd/src/net/osmand/plus'
if not (plus / 'search').is_dir():
    raise SystemExit(f'Invalid OsmAnd checkout: {plus / "search"} does not exist')

quick_search = plus / 'search/QuickSearchHelper.java'
s = quick_search.read_text(encoding='utf-8')
imp = 'import net.osmand.plus.googlemaps.GoogleSearchApi;\n'
marker = 'import net.osmand.plus.OsmandApplication;\n'
if imp not in s:
    if marker not in s:
        raise SystemExit('QuickSearchHelper import anchor not found')
    s = s.replace(marker, marker + imp, 1)
register_anchor = '\t\tcore.registerAPI(new SearchOnlineApi(app));\n'
register_line = register_anchor + '\t\tcore.registerAPI(new GoogleSearchApi(app));\n'
if 'core.registerAPI(new GoogleSearchApi(app));' not in s:
    if register_anchor not in s:
        raise SystemExit('SearchOnlineApi registration anchor not found')
    s = s.replace(register_anchor, register_line, 1)
quick_search.write_text(s, encoding='utf-8')

global_xml = repo / 'OsmAnd/res/xml/global_settings.xml'
x = global_xml.read_text(encoding='utf-8')
xml_marker = '\t<PreferenceCategory\n\t\tandroid:key="other"'
if 'android:key="google_maps_api_key"' not in x:
    if xml_marker not in x:
        raise SystemExit('global_settings Other category anchor not found')
    block = '''\t<PreferenceCategory\n\t\tandroid:key="google_maps_integration"\n\t\tandroid:layout="@layout/preference_category_with_descr"\n\t\tandroid:title="Google Maps" />\n\n\t<net.osmand.plus.settings.preferences.EditTextPreferenceEx\n\t\tandroid:key="google_maps_api_key"\n\t\tandroid:layout="@layout/preference_with_descr"\n\t\tandroid:persistent="false"\n\t\tandroid:title="Google Maps API key"\n\t\tandroid:inputType="textVisiblePassword" />\n\n\t<net.osmand.plus.settings.preferences.SwitchPreferenceEx\n\t\tandroid:key="google_search_enabled"\n\t\tandroid:layout="@layout/preference_with_descr_dialog_and_switch"\n\t\tandroid:persistent="false"\n\t\tandroid:summaryOff="@string/shared_string_off"\n\t\tandroid:summaryOn="@string/shared_string_on"\n\t\tandroid:title="Use Google for address search" />\n\n'''
    x = x.replace(xml_marker, block + xml_marker, 1)
    global_xml.write_text(x, encoding='utf-8')

global_java = plus / 'settings/fragments/GlobalSettingsFragment.java'
g = global_java.read_text(encoding='utf-8')

import_marker = 'import net.osmand.plus.settings.backend.ApplicationMode;\n'
imports = ('import net.osmand.plus.googlemaps.GoogleMapsPreferences;\n'
           'import net.osmand.plus.googlemaps.GoogleSearchPreferences;\n')
if 'import net.osmand.plus.googlemaps.GoogleMapsPreferences;' not in g:
    if import_marker not in g:
        raise SystemExit('GlobalSettingsFragment import anchor not found')
    g = g.replace(import_marker, import_marker + imports, 1)

const_marker = '\tprivate static final String MEDIA_STORAGE_PREF_ID = "media_storage";\n'
consts = ('\tprivate static final String GOOGLE_API_KEY_PREF_ID = "google_maps_api_key";\n'
          '\tprivate static final String GOOGLE_SEARCH_PREF_ID = "google_search_enabled";\n')
if 'GOOGLE_API_KEY_PREF_ID' not in g:
    if const_marker not in g:
        raise SystemExit('GlobalSettingsFragment constant anchor not found')
    g = g.replace(const_marker, const_marker + consts, 1)

setup_marker = '\t\tsetupMediaStoragePref();\n'
setup_calls = '\t\tsetupGoogleApiKeyPref();\n\t\tsetupGoogleSearchPref();\n'
if 'setupGoogleApiKeyPref();' not in g:
    if setup_marker not in g:
        raise SystemExit('GlobalSettingsFragment setup anchor not found')
    g = g.replace(setup_marker, setup_marker + setup_calls, 1)

start = g.find('\t\tif (GOOGLE_API_KEY_PREF_ID.equals(prefId)) {')
if start != -1:
    end_marker = '\n\t\tif (prefId.equals(SEND_ANONYMOUS_DATA_PREF_ID)) {'
    end = g.find(end_marker, start)
    if end != -1:
        g = g[:start] + g[end + 1:]

callback_marker = '\tpublic boolean onPreferenceChange(Preference preference, Object newValue) {\n\t\tString prefId = preference.getKey();\n'
google_callback = '''\t\tif (GOOGLE_API_KEY_PREF_ID.equals(prefId)) {\n\t\t\tif (!(newValue instanceof String)) {\n\t\t\t\treturn false;\n\t\t\t}\n\t\t\tGoogleMapsPreferences.setApiKey(app, (String) newValue);\n\t\t\tsetupGoogleApiKeyPref();\n\t\t\treturn true;\n\t\t} else if (GOOGLE_SEARCH_PREF_ID.equals(prefId)) {\n\t\t\tif (!(newValue instanceof Boolean)) {\n\t\t\t\treturn false;\n\t\t\t}\n\t\t\tGoogleSearchPreferences.setGoogleSearchEnabled(app, (Boolean) newValue);\n\t\t\treturn true;\n\t\t}\n\n'''
if callback_marker not in g:
    raise SystemExit('existing onPreferenceChange callback anchor not found')
if 'GoogleMapsPreferences.setApiKey(app, (String) newValue);' not in g:
    g = g.replace(callback_marker, callback_marker + google_callback, 1)

setup_helpers_marker = '\tprivate void setupDefaultAppModePref() {'
setup_helpers = '''\tprivate void setupGoogleApiKeyPref() {\n\t\tandroidx.preference.EditTextPreference preference = findPreference(GOOGLE_API_KEY_PREF_ID);\n\t\tif (preference != null) {\n\t\t\tString key = GoogleMapsPreferences.getApiKey(app);\n\t\t\tpreference.setText(key);\n\t\t\tpreference.setSummary(key.isEmpty() ? "Not configured" : "Configured");\n\t\t}\n\t}\n\n\tprivate void setupGoogleSearchPref() {\n\t\tandroidx.preference.SwitchPreferenceCompat preference = findPreference(GOOGLE_SEARCH_PREF_ID);\n\t\tif (preference != null) {\n\t\t\tpreference.setChecked(GoogleSearchPreferences.isGoogleSearchEnabled(app));\n\t\t}\n\t}\n\n'''
if 'private void setupGoogleApiKeyPref()' not in g:
    if setup_helpers_marker not in g:
        raise SystemExit('GlobalSettingsFragment helper insertion anchor not found')
    g = g.replace(setup_helpers_marker, setup_helpers + setup_helpers_marker, 1)

global_java.write_text(g, encoding='utf-8')

# Address OCR route integration.
integration_root = Path(__file__).resolve().parents[1]
address_package = plus / 'addressocr'
address_package.mkdir(parents=True, exist_ok=True)
for name in ('AddressOcrRouteActivity.java', 'AddressOcrRouteLauncher.java'):
    source = integration_root / 'OsmAnd/src/net/osmand/plus/addressocr' / name
    if not source.is_file():
        raise SystemExit(f'Missing OCR integration source: {source}')
    shutil.copy2(source, address_package / name)

# ML Kit bundled OCR model, executed locally on the device.
gradle = repo / 'OsmAnd/build.gradle'
g = gradle.read_text(encoding='utf-8')
dep = "\timplementation 'com.google.mlkit:text-recognition:16.0.1'"
if dep not in g:
    marker = 'dependencies {\n'
    if marker not in g:
        raise SystemExit('OsmAnd/build.gradle dependencies block not found')
    g = g.replace(marker, marker + dep + '\n', 1)
    gradle.write_text(g, encoding='utf-8')

# The base manifest already declares CAMERA permission. Register the scanner activity.
manifest = repo / 'OsmAnd/AndroidManifest.xml'
m = manifest.read_text(encoding='utf-8')
activity_decl = '    <activity android:name=".addressocr.AddressOcrRouteActivity" android:exported="false" />'
if activity_decl not in m:
    marker = '<application'
    pos = m.find(marker)
    if pos < 0:
        raise SystemExit('AndroidManifest.xml application element not found')
    end = m.find('>', pos)
    if end < 0:
        raise SystemExit('AndroidManifest.xml application opening tag not found')
    m = m[:end + 1] + '\n' + activity_decl + m[end + 1:]
    manifest.write_text(m, encoding='utf-8')

# Put a small OCR button on the map screen. It launches the scanner; the scanner
# appends successfully geocoded addresses to the existing route.
map_activity = plus / 'activities/MapActivity.java'
s = map_activity.read_text(encoding='utf-8')
imp = 'import net.osmand.plus.addressocr.AddressOcrRouteLauncher;'
if imp not in s:
    anchor = 'import net.osmand.plus.AppInitializeListener;'
    if anchor not in s:
        raise SystemExit('MapActivity import anchor not found')
    s = s.replace(anchor, anchor + '\n' + imp, 1)
if 'AddressOcrRouteLauncher.install(this);' not in s:
    class_pos = s.find('public class MapActivity')
    if class_pos < 0:
        raise SystemExit('MapActivity class declaration not found')
    match = re.search(r'protected\s+void\s+onCreate\s*\(\s*@Nullable\s+Bundle\s+savedInstanceState\s*\)', s[class_pos:])
    if not match:
        match = re.search(r'(?:protected|public)\s+void\s+onCreate\s*\(\s*[^)]*Bundle\s+savedInstanceState\s*\)', s[class_pos:])
    if not match:
        raise SystemExit('MapActivity.onCreate not found')
    method_start = class_pos + match.start()
    super_pos = s.find('super.onCreate(savedInstanceState);', method_start)
    if super_pos < 0:
        raise SystemExit('MapActivity super.onCreate not found')
    insert_at = super_pos + len('super.onCreate(savedInstanceState);')
    s = s[:insert_at] + '\n\t\tAddressOcrRouteLauncher.install(this);' + s[insert_at:]
    map_activity.write_text(s, encoding='utf-8')

print('Google integration and address OCR route integration applied successfully')
