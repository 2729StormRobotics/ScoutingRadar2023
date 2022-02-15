package org.stormroboticsnj.scoutingradar2022;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigureScoutingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigureScoutingFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityResultLauncher<String> fileResultLauncher;
    private Context mContext;

    private SharedPreferences mSharedPreferences;

    public ConfigureScoutingFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unused")
    public static ConfigureScoutingFragment newInstance() {
        return new ConfigureScoutingFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        fileResultLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    String path = result.getPath();
                    // Not solid checking at all, but better than nothing?
                    if (path.substring(path.lastIndexOf('.') + 1).equals("ini")) {
                        processIniFile(result);
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_configure_scouting, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button uploadButton = view.findViewById(R.id.configure_button_upload);
        uploadButton.setOnClickListener((v) -> fileResultLauncher.launch("*/*"));
    }

    @Override
    public void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void processIniFile(Uri input) {
        new Thread(() -> {
            try {
                // Place to save the new info
                Set<String> subSpinners = new HashSet<>();
                Set<String> pitSpinners = new HashSet<>();
                Set<String> objSpinners = new HashSet<>();

                // This warning is because Ini implements Map and the linter doesn't realize that
                // the constructor builds data
                @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
                Ini ini = new Ini(mContext.getContentResolver().openInputStream(input));

                // One section at a time for the ini file
                Profile.Section section = ini.get("subjective");
                if (section != null) {
                    // Given format:
                    // key:value is [spinner name]:[spinner option],[spinner option], ... [spinner option]
                    Set<String> keys = section.keySet();
                    for (String s : keys) {
                        // Replace underscores with spaces to display cleanly
                        String name = s.replace('_', ' ');
                        String values = section.get(s);

                        // Store in the same aforementioned format
                        subSpinners.add(name + ":" + values);
                    }
                }

                section = ini.get("pit");
                if (section != null) {
                    // Given format:
                    // key:value is [spinner name]:[spinner option],[spinner option], ... [spinner option]
                    Set<String> keys = section.keySet();
                    for (String s : keys) {
                        // Replace underscores with spaces to display cleanly
                        String name = s.replace('_', ' ');
                        String values = section.get(s);

                        // Store in the same aforementioned format
                        pitSpinners.add(name + ":" + values);
                    }
                }

                section = ini.get("objective_spinners");
                if (section != null) {
                    // Given format:
                    // key:value is [spinner name]:[spinner option],[spinner option], ... [spinner option]
                    Set<String> keys = section.keySet();
                    for (String s : keys) {
                        // Replace underscores with spaces to display cleanly
                        String name = s.replace('_', ' ');
                        String values = section.get(s);

                        // Store in the same aforementioned format
                        objSpinners.add(name + ":" + values);
                    }
                }

                mSharedPreferences.edit()
                                  .putStringSet(getString(R.string.pref_key_sub_spinner),
                                          subSpinners)
                                  .putStringSet(getString(R.string.pref_key_pit_spinner),
                                          pitSpinners)
                                  .putStringSet(getString(R.string.pref_key_obj_spinner),
                                          objSpinners)
                                  .apply();


            } catch (IOException e) {
                MainActivity a = (MainActivity) getActivity();
                if (a != null) {
                    a.runOnUiThread(() -> Toast.makeText(a, getString(R.string.error_reading_file),
                            Toast.LENGTH_LONG).show());
                }
                Log.e("Configure", "Error reading file", e);
            }
        }).start();
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
        if (key.equals(getString(R.string.pref_key_team))) {
            requireActivity().recreate();
        }
    }
}