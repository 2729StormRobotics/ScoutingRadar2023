package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import static org.stormroboticsnj.scoutingradar2022.UiUtils.SpinnerInfo;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.stormroboticsnj.scoutingradar2022.R;
import org.stormroboticsnj.scoutingradar2022.UiUtils;
import org.stormroboticsnj.scoutingradar2022.database.DataUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PitScoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PitScoutFragment extends Fragment {

    private static final int VERTICAL_MARGIN = 8;
    private SpinnerInfo[] mSpinnerInfos;
    private PitScoutViewModel mActionsViewModel;
    private Context mContext;
    // ConstraintLayout
    private ConstraintLayout mConstraintLayout;
    // Actions List TextView
    private UiUtils.TextInputWrapper mTeamNumTextInput;
    private UiUtils.TextInputWrapper mNotesTextInput;
    private int mConstraintLayoutId;
    private Button mSubmitButton;

    private String[] SPINNER_NAMES;
    private String[][] SPINNER_CONTENTS;


    public PitScoutFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unused")
    public static PitScoutFragment newInstance() {
        return new PitScoutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pit_scout, container, false);
        mConstraintLayout = v.findViewById(R.id.layout_pit_main);
        mConstraintLayoutId = mConstraintLayout.getId();

        mTeamNumTextInput =
                new UiUtils.TextInputWrapper(v.findViewById(R.id.pit_text_input_team_num));
        mNotesTextInput = new UiUtils.TextInputWrapper(v.findViewById(R.id.pit_text_input_notes));

        return v;
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Get the SharedPreferences
        SharedPreferences
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // Get all of the spinner contents
        Set<String> set = sharedPreferences.getStringSet(getString(R.string.pref_key_pit_spinner),
                null);

        if (set != null) {
            SPINNER_CONTENTS = new String[set.size()][];
            SPINNER_NAMES = new String[set.size()];
            // Split the spinner contents into arrays
            int i = 0;
            for (String s : set) {
                String[] split = s.split(":");
                SPINNER_CONTENTS[i] = split[1].replace('_', ' ').split(",");
                SPINNER_NAMES[i] = split[0];
                i++;
            }


            // Uses bubble sort algorithm to sort the spinner names
            // Orders it using the ordering number at the beginning
            // Also going to get rid of periods
            String temp;
            for (int j = 0; j < SPINNER_NAMES.length; j++) {
                for (int k = 0; k < SPINNER_NAMES.length; k++) {
                    String stringJ = SPINNER_NAMES[j].substring(0,2);
                    if (stringJ.contains(".")){
                        stringJ = stringJ.replace(".", "");
                    }
                    int intJ = Integer.valueOf(parseInt(stringJ));

                    String stringK = SPINNER_NAMES[k].substring(0,2);
                    if (stringK.contains(".")){
                        stringK = stringK.replace(".", "");
                    }
                    int intK = Integer.valueOf(parseInt(stringK));

                    if (intJ < intK) {
                        temp = SPINNER_NAMES[j];
                        SPINNER_NAMES[j] = SPINNER_NAMES[k];
                        SPINNER_NAMES[k] = temp;
                    }
                }
            }

            // Uses bubble sort algorithm to sort the spinner contents
            // Orders it using the ordering number at the beginning
            // Also going to get rid of periods
            String[] temp2;
            for (int j = 0; j < SPINNER_CONTENTS.length; j++) {
                for (int k = 0; k < SPINNER_CONTENTS.length; k++) {
                    String stringJ = SPINNER_CONTENTS[j][0].substring(0,2);
                    if (stringJ.contains(".")){
                        stringJ = stringJ.replace(".", "");
                    }
                    int intJ = Integer.valueOf(parseInt(stringJ));

                    String stringK = SPINNER_CONTENTS[k][0].substring(0,2);
                    if (stringK.contains(".")){
                        stringK = stringK.replace(".", "");
                    }
                    int intK = Integer.valueOf(parseInt(stringK));

                    if (intJ < intK) {
                        temp2 = SPINNER_CONTENTS[j];
                        SPINNER_CONTENTS[j] = SPINNER_CONTENTS[k];
                        SPINNER_CONTENTS[k] = temp2;

                    }
                }
            }
            // Replaces the 1s and dots in the contents with ""
            for (int j = 0; j < SPINNER_CONTENTS.length; j++) {
                String toBeReplaced = SPINNER_CONTENTS[j][0].substring(0, 1);
                if (!(toBeReplaced.contains("1"))) {
                    SPINNER_CONTENTS[j][0] = SPINNER_CONTENTS[j][0].replace(toBeReplaced, "");

                }

            }

            for (int j = 0; j < SPINNER_CONTENTS.length; j++) {
                String toBeReplaced = SPINNER_CONTENTS[j][0].substring(0,2);
                SPINNER_CONTENTS[j][0] = SPINNER_CONTENTS[j][0].replace(toBeReplaced, "");

            }

        } else {
            String[] arr = getResources().getStringArray(R.array.pit_spinners);
            SPINNER_CONTENTS = new String[arr.length][];
            SPINNER_NAMES = new String[arr.length];
            // Split the spinner contents into arrays
            int i = 0;
            for (String s : arr) {
                String[] split = s.split(":");
                SPINNER_CONTENTS[i] = split[1].replace('_', ' ').split(",");
                SPINNER_NAMES[i] = split[0];
                i++;
            }
        }

        // Generate the UI
        generateUI();
        // Make the ViewModel
        mActionsViewModel = new ViewModelProvider(this).get(PitScoutViewModel.class);
    }

    public void generateUI() {

        // Reusable ConstraintSet
        ConstraintSet constraintSet = new ConstraintSet();
        // Spinners
        mSpinnerInfos = new SpinnerInfo[SPINNER_NAMES.length];
        // Set up first spinner
        mSpinnerInfos[0] = mSpinnerInfos[0] =
                setupNewSpinner(0, constraintSet, mNotesTextInput.getInputLayout().getId());
        // Set up the rest of the spinners
        for (int i = 1; i < SPINNER_NAMES.length; i++) {
            mSpinnerInfos[i] =
                    setupNewSpinner(i, constraintSet, mSpinnerInfos[i - 1].id);
        }

        mSubmitButton = setupSubmitButton(constraintSet,
                mSpinnerInfos[mSpinnerInfos.length - 1].id);

    }

    public void onButtonClick(View view) {
        if (view.getId() == mSubmitButton.getId()) {
            submit();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void submit() {
        if (validateNumberTextInput(mTeamNumTextInput,
                getString(R.string.error_no_teamnum),
                getString(R.string.error_notnumber_teamnum),
                getString(R.string.error_toolong_teamnum))) {

            // Add the Spinner Info
            for (SpinnerInfo spinnerInfo : mSpinnerInfos) {
                mActionsViewModel.addAction(new DataUtils.Action(spinnerInfo.name,
                        spinnerInfo.contents[spinnerInfo.spinner.getSelectedItemPosition()]));
            }

            mActionsViewModel.processAndSaveData(
                    parseInt(Objects.requireNonNull(mTeamNumTextInput.getEditText(),
                            "NO TEAM NUM EDIT TEXT").getText().toString()),
                    Objects.requireNonNull(mNotesTextInput.getEditText(), "NO NOTES EDIT TEXT")
                           .getText()
                           .toString());

            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                      .navigate(R.id.action_pitScoutFragment_to_matchRecordFragment);

        } else {
            // Toast error
            Toast.makeText(mContext, "Errors found.", Toast.LENGTH_SHORT).show();
        }
    }

    private Button setupSubmitButton(ConstraintSet constraintSet, int previousId) {
        // Create the button
        Button button = new MaterialButton(mContext);
        // Generate a unique id for the button
        int buttonId = View.generateViewId();
        button.setId(buttonId);
        // Set the text of the button
        button.setText(getString(R.string.button_submit));
        // This fragment is the listener for the button
        button.setOnClickListener(this::onButtonClick);
        // Add the button to the layout
        mConstraintLayout.addView(button);

        // Set the constraints for the button
        constraintSet.clone(mConstraintLayout);
        // Connect the button to the previous button
        chainViewsVertically(constraintSet, previousId, buttonId);
        // Center the button horizontally
        centerViewHorizontally(constraintSet, buttonId);
        // Apply the constraints
        constraintSet.applyTo(mConstraintLayout);

        return button;
    }

    private SpinnerInfo setupNewSpinner(int index, ConstraintSet constraintSet, int previousId) {
        // Create the spinner
        Spinner spinner = new Spinner(mContext);
        // Generate a unique id for the spinner
        int spinnerId = View.generateViewId();
        spinner.setId(spinnerId);

        // Create a title view for the spinner
        TextView textView = new TextView(mContext);
        int textId = View.generateViewId();
        textView.setId(textId);
        textView.setText(SPINNER_NAMES[index]);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextSize(2, 20);
        // Create the adapter for the spinner
        spinner.setAdapter(new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_dropdown_item, SPINNER_CONTENTS[index]));
        // Add the spinner to the layout
        mConstraintLayout.addView(spinner);
        // Add the textview to the layout
        mConstraintLayout.addView(textView);

        // Set the constraints for the spinner
        constraintSet.clone(mConstraintLayout);

        constraintSet.connect(textId, ConstraintSet.LEFT, mConstraintLayoutId, ConstraintSet.LEFT,
                0);
        constraintSet.connect(textId, ConstraintSet.RIGHT, spinnerId, ConstraintSet.LEFT, 8);
        constraintSet.connect(spinnerId, ConstraintSet.LEFT, textId, ConstraintSet.RIGHT, 0);
        constraintSet.connect(spinnerId, ConstraintSet.RIGHT, mConstraintLayoutId,
                ConstraintSet.RIGHT, 0);
        constraintSet.connect(textId, ConstraintSet.TOP, spinnerId, ConstraintSet.TOP);
        constraintSet.connect(textId, ConstraintSet.BOTTOM, spinnerId, ConstraintSet.BOTTOM);

        // Connect the spinner to the previous spinner
        chainViewsVertically(constraintSet, previousId, spinnerId);

        // Apply the constraints
        constraintSet.applyTo(mConstraintLayout);

        //textView.setHeight(spinner.getHeight());
        textView.setGravity(Gravity.CENTER);

        // Return the spinner info
        return new SpinnerInfo(SPINNER_NAMES[index], SPINNER_CONTENTS[index], spinnerId, spinner);

    }

    private void centerViewHorizontally(ConstraintSet constraintSet, int viewId) {
        constraintSet.connect(viewId, ConstraintSet.LEFT, mConstraintLayoutId,
                ConstraintSet.LEFT,
                0);
        constraintSet.connect(viewId, ConstraintSet.RIGHT, mConstraintLayoutId,
                ConstraintSet.RIGHT,
                0);
    }

    private void chainViewsVertically(ConstraintSet constraintSet, int topId, int bottomId) {
        constraintSet.connect(bottomId, ConstraintSet.TOP, topId, ConstraintSet.BOTTOM,
                VERTICAL_MARGIN);
    }

    /**
     * Checks for and displays errors on a given number-input {@link TextInputLayout} and creates a
     * {@link org.stormroboticsnj.scoutingradar2022.scoutingfragments.SmallerTextWatcher} to dismiss the error once it is corrected.
     * <p>
     * The parameters for validation are that the entered input must be 1. Not Empty 2. Digits Only
     * 3. Less than the set max length, if one is set as an attribute of the TextInputLayout.
     *
     * @param textInputWrapper TextInputLayout to monitor
     * @param emptyError       The error message to be displayed if this TextInputLayout is empty.
     * @return whether or not an error was found
     */
    public boolean validateNumberTextInput(final UiUtils.TextInputWrapper textInputWrapper, String emptyError, String notNumberError, String tooLongError) {
        boolean isError = false;
        String input = textInputWrapper.getEditText().getText().toString();

        /* Check to make sure the entered text exists, is a number, and is shorter than
           the set max length */
        if (TextUtils.isEmpty(input)) {
            isError = true;
            textInputWrapper.getInputLayout().setError(emptyError);
        } else if (!TextUtils.isDigitsOnly(input)) {
            isError = true;
            textInputWrapper.getInputLayout().setError(notNumberError);
        } else if (textInputWrapper.getInputLayout().getCounterMaxLength() > 0 &&
                   input.length() > textInputWrapper.getInputLayout().getCounterMaxLength()) {
            isError = true;
            textInputWrapper.getInputLayout().setError(tooLongError);
        }

        //  If errors were found, add a listener to dismiss the error once it is corrected
        if (isError) {
            textInputWrapper.setTextWatcher(
                    new org.stormroboticsnj.scoutingradar2022.scoutingfragments.SmallerTextWatcher(
                            textInputWrapper.getInputLayout()) {
                        @Override
                        public void afterTextChanged(
                                String input, TextInputLayout layout,
                                EditText editText) {
                    /* Text has no errors if:
                       1. Not Empty
                       2. Digits Only
                       3. Either there is no max or length is <= max
                     */
                            if ((!TextUtils.isEmpty(input) && TextUtils.isDigitsOnly(
                                    input)) && (layout.getCounterMaxLength() <= 0 ||
                                                input.length() <= layout.getCounterMaxLength())) {
                                layout.setError(null);
                                textInputWrapper.removeTextWatcher(this);
                            }
                        }
                    });
        }

        return !isError;
    }


}