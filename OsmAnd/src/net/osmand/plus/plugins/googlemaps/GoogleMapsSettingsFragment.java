package net.osmand.plus.plugins.googlemaps;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import net.osmand.plus.R;
import net.osmand.plus.plugins.PluginsHelper;
import net.osmand.plus.settings.fragments.BaseSettingsFragment;

public class GoogleMapsSettingsFragment extends BaseSettingsFragment {
    @Override
    protected void setupPreferences() {
        addPreferencesFromResource(R.xml.google_maps_settings);
        GoogleMapsPlugin plugin = (GoogleMapsPlugin) PluginsHelper.getPlugin(GoogleMapsPlugin.PLUGIN_ID);
        if (plugin == null) {
            return;
        }

        EditTextPreference key = findPreference(plugin.API_KEY.getId());
        if (key != null) {
            key.setText(plugin.API_KEY.get());
            key.setSummary(plugin.API_KEY.get().isEmpty() ? R.string.shared_string_none : "Configured");
        }

        ListPreference search = findPreference(plugin.SEARCH_MODE.getId());
        if (search != null) {
            search.setValue(plugin.SEARCH_MODE.get().name());
        }

        ListPreference map = findPreference(plugin.MAP_MODE.getId());
        if (map != null) {
            map.setValue(plugin.MAP_MODE.get().name());
        }
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        GoogleMapsPlugin plugin = (GoogleMapsPlugin) PluginsHelper.getPlugin(GoogleMapsPlugin.PLUGIN_ID);
        if (plugin == null) {
            return false;
        }
        if (preference.getKey().equals(plugin.API_KEY.getId())) {
            plugin.API_KEY.set(String.valueOf(newValue).trim());
            preference.setSummary(plugin.API_KEY.get().isEmpty() ? R.string.shared_string_none : "Configured");
            return true;
        }
        if (preference.getKey().equals(plugin.SEARCH_MODE.getId())) {
            plugin.SEARCH_MODE.set(GoogleMapsPlugin.SearchMode.valueOf(String.valueOf(newValue)));
            return true;
        }
        if (preference.getKey().equals(plugin.MAP_MODE.getId())) {
            plugin.MAP_MODE.set(GoogleMapsPlugin.MapMode.valueOf(String.valueOf(newValue)));
            PluginsHelper.refreshLayers(requireContext(), getMapActivity());
            return true;
        }
        return super.onPreferenceChange(preference, newValue);
    }
}
