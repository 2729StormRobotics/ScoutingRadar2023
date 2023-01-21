package org.stormroboticsnj.scoutingradar2022;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private ActivityResultLauncher<String[]> fileResultLauncher;
    private Context mContext;

    private SharedPreferences mSharedPreferences;
    private TextView mCurrentPrefsTextView;

    // Cache current configuration
    private String mObjButtons = "";
    private String mObjAbbreviations = "";
    private String mObjSpinners = "";
    private String mSubButtons = "";
    private String mSubAbbreviations = "";
    private String mSubSpinners = "";
    private String mPitSpinners = "";

    public ConfigureScoutingFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unused")
    public static ConfigureScoutingFragment newInstance() {
        return new ConfigureScoutingFragment();
    }

    private static String formatArray(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            s = s.replace(",", ", ");
            s = s.replace(":", ": ");
            s = s.replace("_", " ");
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        //                    String path = result.getPath();
        // Not solid checking at all, but better than nothing?
        //                    if (path.substring(path.lastIndexOf('.') + 1).equals("ini")) {
        //                    }
        fileResultLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                this::processIniFile);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
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
        uploadButton.setOnClickListener((v) -> fileResultLauncher.launch(new String[]{"*/*"}));

        TextView messageTextView = view.findViewById(R.id.configure_text_message);
        // Activate URL within this TextView
        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());

        mCurrentPrefsTextView = view.findViewById(R.id.configure_text_current);
        // Get the current configuration
        readAllPrefs();
        // Display the current configuration to the user
        updateConfigTextView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void updateConfigTextView() {
        mCurrentPrefsTextView.setText(R.string.configure_current_header);
        mCurrentPrefsTextView.append("\nObjective Buttons\n");
        mCurrentPrefsTextView.append(mObjButtons);
        mCurrentPrefsTextView.append("\n" + mObjAbbreviations + "\n");
        mCurrentPrefsTextView.append("\nObjective Spinners\n");
        mCurrentPrefsTextView.append(mObjSpinners);
        mCurrentPrefsTextView.append("\nSubjective Buttons\n");
        mCurrentPrefsTextView.append(mSubButtons);
        mCurrentPrefsTextView.append("\n" + mSubAbbreviations + "\n");
        mCurrentPrefsTextView.append("\nSubjective Spinners\n");
        mCurrentPrefsTextView.append(mSubSpinners);
        mCurrentPrefsTextView.append("\nPit Spinners\n");
        mCurrentPrefsTextView.append(mPitSpinners);
    }

    private void readAllPrefs() {
        mObjButtons = formatPrefString(
                mSharedPreferences.getString(getString(R.string.pref_key_obj_buttons),
                        getString(R.string.obj_buttons_default)));
        mObjAbbreviations = formatPrefString(
                mSharedPreferences.getString(getString(R.string.pref_key_obj_abbrs),
                        getString(R.string.obj_abbrs_default)));
        mObjSpinners = formatArray(readPrefs(R.string.pref_key_obj_spinner, R.array.obj_spinners));
        mSubSpinners = formatArray(readPrefs(R.string.pref_key_sub_spinner, R.array.sub_spinners));
        mPitSpinners = formatArray(readPrefs(R.string.pref_key_pit_spinner, R.array.pit_spinners));
    }

    private String formatPrefString(String s) {
        return s.replace(",", ", ").replace('_', ' ');
    }

    private String[] readPrefs(int prefKey, int defaultArray) {
        // Get all of the spinner contents
        Set<String> set = mSharedPreferences.getStringSet(getString(prefKey),
                null);
        String[] prefArray;
        if (set != null) {
            // Convert to array
            prefArray = set.toArray(new String[0]);
        } else {
            // Preference has never been set; use default options.
            prefArray = getResources().getStringArray(defaultArray);
        }
        return prefArray;
    }

    private void processIniFile(Uri input) {
        new Thread(() -> {
            try {
                // Place to save the new info
                Set<String> subSpinners = new HashSet<>();
                Set<String> pitSpinners = new HashSet<>();
                Set<String> objSpinners = new HashSet<>();
                String objButtons = "";
                String subButtons = "";
                String objAbbreviations = "";
                String subAbbreviations = "";

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

                section = ini.get("objective_buttons");
                if (section != null) {
                    // Given format:
                    // key:value is [button name]:[button abbreviation]
                    // But this needs to be ordered

                    objButtons = section.get("Names");

                    objAbbreviations = section.get("Abbreviations");
                }
                // Subjective Buttons
                section = ini.get("subjective_buttons");
                if (section != null) {
                    // Given format:
                    // key:value is [button name]:[button abbreviation]
                    // But this needs to be ordered

                    subButtons = section.get("Names");

                    subAbbreviations = section.get("Abbreviations");
                }
                SharedPreferences.Editor e = mSharedPreferences.edit();
                e.putStringSet(getString(R.string.pref_key_sub_spinner), subSpinners)
                 .putStringSet(getString(R.string.pref_key_pit_spinner), pitSpinners)
                 .putStringSet(getString(R.string.pref_key_obj_spinner), objSpinners);

                if (objAbbreviations != null) {
                    e.putString(getString(R.string.pref_key_obj_abbrs), objAbbreviations);
                }

                if (subAbbreviations != null) {
                    e.putString(getString(R.string.pref_key_sub_abbrs), subAbbreviations);
                }

                if (objButtons != null) {
                    e.putString(getString(R.string.pref_key_obj_buttons), objButtons);
                }

                if (subButtons != null) {
                    e.putString(getString(R.string.pref_key_sub_buttons), subButtons);
                }

                e.apply();
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
        if (getString(R.string.pref_key_obj_buttons).equals(key)) {
            mObjButtons = formatPrefString(
                    mSharedPreferences.getString(getString(R.string.pref_key_obj_buttons),
                            getString(R.string.obj_buttons_default)));
        } else if (getString(R.string.pref_key_obj_abbrs).equals(key)) {
            mObjAbbreviations = formatPrefString(
                    mSharedPreferences.getString(getString(R.string.pref_key_obj_abbrs),
                            getString(R.string.obj_abbrs_default)));
        }  else if (getString(R.string.pref_key_sub_buttons).equals(key)) {
        mSubButtons = formatPrefString(
                mSharedPreferences.getString(getString(R.string.pref_key_sub_buttons),
                        getString(R.string.obj_buttons_default)));
    } else if (getString(R.string.pref_key_sub_abbrs).equals(key)) {
            mSubAbbreviations = formatPrefString(
                    mSharedPreferences.getString(getString(R.string.pref_key_sub_abbrs),
                            getString(R.string.obj_abbrs_default)));
        } else if (getString(R.string.pref_key_obj_spinner).equals(key)) {
            mObjSpinners =
                    formatArray(readPrefs(R.string.pref_key_obj_spinner, R.array.obj_spinners));
        } else if (getString(R.string.pref_key_sub_spinner).equals(key)) {
            mSubSpinners =
                    formatArray(readPrefs(R.string.pref_key_sub_spinner, R.array.sub_spinners));
        } else if (getString(R.string.pref_key_pit_spinner).equals(key)) {
            mPitSpinners =
                    formatArray(readPrefs(R.string.pref_key_pit_spinner, R.array.pit_spinners));
        }


        updateConfigTextView();
    }

}