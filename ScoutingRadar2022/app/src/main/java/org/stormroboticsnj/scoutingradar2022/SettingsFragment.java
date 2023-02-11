package org.stormroboticsnj.scoutingradar2022;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_dark))) {
            String string = sharedPreferences.getString(key, "");
            if (getString(R.string.pref_value_dark_dark).equals(string)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            } else if (getString(R.string.pref_value_dark_light).equals(string)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (getString(R.string.pref_value_dark_system).equals(string)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }
        else if (key.equals(getString(R.string.pref_key_team))) {
            requireActivity().recreate();
        }
        else if (key.equals(getString(R.string.pref_key_teamnum))) {
            if (sharedPreferences.getString(key, "").equals("2729")) {
                Activity a = getActivity();
                if (a != null) {
                    Toast.makeText(a, "Clap Clap Whoosh!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences(),
                "NO Shared preferences").registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences(),
                "NO Shared preferences").unregisterOnSharedPreferenceChangeListener(this);
    }
}