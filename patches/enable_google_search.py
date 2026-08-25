from pathlib import Path

# The workflow copies this script into osmand-source/tools before executing it.
# Resolve the OsmAnd checkout from the script location rather than from the
# process working directory.
repo = Path(__file__).resolve().parents[1]
plus = repo / 'OsmAnd/src/net/osmand/plus'

# 1. Register Google Places as an optional OsmAnd search API.
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

# 2. Add the API-key field and explicit Google-search switch to OsmAnd's
#    existing Global Settings screen. These are normal OsmAnd preferences,
#    not a separate hidden activity.
global_xml = repo / 'OsmAnd/res/xml/global_settings.xml'
x = global_xml.read_text(encoding='utf-8')
xml_marker = '\t<PreferenceCategory\n\t\tandroid:key="other"'
xml_insert = '''\t<PreferenceCategory\n\t\tandroid:key="google_maps_integration"\n\t\tandroid:layout="@layout/preference_category_with_descr"\n\t\tandroid:title="Google Maps" />\n\n\t<net.osmand.plus.settings.preferences.EditTextPreferenceEx\n\t\tandroid:key="google_maps_api_key"\n\t\tandroid:layout="@layout/preference_with_descr"\n\t\tandroid:persistent="false"\n\t\tandroid:title="Google Maps API key"\n\t\tandroid:inputType="textVisiblePassword" />\n\n\tnet.osmand.plus.settings.preferences.SwitchPreferenceEx\n'''.replace('\n\t\tnet.osmand', '\n\t<net.osmand')
# Correctly place the two preferences immediately before the existing Other category.
if 'android:key="google_maps_api_key"' not in x:
    if xml_marker not in x:
        raise SystemExit('global_settings Other category anchor not found')
    block = '''\t<PreferenceCategory\n\t\tandroid:key="google_maps_integration"\n\t\tandroid:layout="@layout/preference_category_with_descr"\n\t\tandroid:title="Google Maps" />\n\n\t<net.osmand.plus.settings.preferences.EditTextPreferenceEx\n\t\tandroid:key="google_maps_api_key"\n\t\tandroid:layout="@layout/preference_with_descr"\n\t\tandroid:persistent="false"\n\t\tandroid:title="Google Maps API key"\n\t\tandroid:inputType="textVisiblePassword" />\n\n\t<net.osmand.plus.settings.preferences.SwitchPreferenceEx\n\t\tandroid:key="google_search_enabled"\n\t\tandroid:layout="@layout/preference_with_descr_dialog_and_switch"\n\t\tandroid:persistent="false"\n\t\tandroid:summaryOff="@string/shared_string_off"\n\t\tandroid:summaryOn="@string/shared_string_on"\n\t\tandroid:title="Use Google for address search" />\n\n'''
    x = x.replace(xml_marker, block + xml_marker, 1)
    global_xml.write_text(x, encoding='utf-8')

# 3. Wire those preferences to our private SharedPreferences store.
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

change_marker = '\t\tString prefId = preference.getKey();\n\n\t\tif (prefId.equals(SEND_ANONYMOUS_DATA_PREF_ID)) {'
change_code = '''\t\tString prefId = preference.getKey();\n\n\t\tif (GOOGLE_API_KEY_PREF_ID.equals(prefId)) {\n\t\t\tGoogleMapsPreferences.setApiKey(app, (String) newValue);\n\t\t\tsetupGoogleApiKeyPref();\n\t\t\treturn true;\n\t\t} else if (GOOGLE_SEARCH_PREF_ID.equals(prefId)) {\n\t\t\tGoogleSearchPreferences.setGoogleSearchEnabled(app, (Boolean) newValue);\n\t\t\treturn true;\n\t\t} else if (prefId.equals(SEND_ANONYMOUS_DATA_PREF_ID)) {'''
if 'GOOGLE_SEARCH_PREF_ID.equals(prefId)' not in g:
    if change_marker not in g:
        raise SystemExit('GlobalSettingsFragment preference-change anchor not found')
    g = g.replace(change_marker, change_code, 1)

method_marker = '\tprivate void setupDefaultAppModePref() {'
methods = '''\tprivate void setupGoogleApiKeyPref() {\n\t\tandroidx.preference.Preference preference = findPreference(GOOGLE_API_KEY_PREF_ID);\n\t\tif (preference instanceof net.osmand.plus.settings.preferences.EditTextPreferenceEx) {\n\t\t\tnet.osmand.plus.settings.preferences.EditTextPreferenceEx edit =\n\t\t\t\t\t(net.osmand.plus.settings.preferences.EditTextPreferenceEx) preference;\n\t\t\tString key = GoogleMapsPreferences.getApiKey(app);\n\t\t\tedit.setText(key);\n\t\t\tedit.setSummary(key.isEmpty() ? "Not configured" : "Configured");\n\t\t}\n\t}\n\n\tprivate void setupGoogleSearchPref() {\n\t\tandroidx.preference.Preference preference = findPreference(GOOGLE_SEARCH_PREF_ID);\n\t\tif (preference instanceof net.osmand.plus.settings.preferences.SwitchPreferenceEx) {\n\t\t\tnet.osmand.plus.settings.preferences.SwitchPreferenceEx toggle =\n\t\t\t\t\t(net.osmand.plus.settings.preferences.SwitchPreferenceEx) preference;\n\t\t\ttoggle.setChecked(GoogleSearchPreferences.isGoogleSearchEnabled(app));\n\t\t}\n\t}\n\n'''
if 'private void setupGoogleApiKeyPref()' not in g:
    if method_marker not in g:
        raise SystemExit('GlobalSettingsFragment method anchor not found')
    g = g.replace(method_marker, methods + method_marker, 1)

global_java.write_text(g, encoding='utf-8')
print('Google search registration and Google Maps settings UI patched successfully')
