package org.stormroboticsnj.scoutingradar2022.scoutingfragments;


import static org.stormroboticsnj.scoutingradar2022.database.DataUtils.Action;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import org.stormroboticsnj.scoutingradar2022.R;

import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ObjectiveMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ObjectiveMatchFragment extends Fragment {

    private static final int BUTTON_MARGIN = 8;

    private UiUtils.ButtonInfo[] mButtonInfos;
    private UiUtils.SpinnerInfo[] mSpinnerInfos;

    private String[] mSpinnerNames;
    private String[][] mSpinnerContents;
    private String[] mButtonNames;
    private String[] mButtonAbbreviations;
    private boolean hasSpinners;
    private boolean hasButtons;

    // ViewModel :)
    private ObjectiveScoutingViewModel mActionsViewModel;
    // Context :))
    private Context mContext;

    // Chronometer
    private Chronometer mChronometer;
    // ConstraintLayout
    private ConstraintLayout mConstraintLayout;
    private int mConstraintLayoutId;
    // Actions List TextView
    private TextView mActionsListView;

    // Input Views
    private UiUtils.TextInputWrapper mTeamNumTextInput;
    private UiUtils.TextInputWrapper mMatchNumTextInput;

    private UiUtils.ToggleGroupWrapper mAllianceToggleGroup;
    private MaterialButton mRedButton;
    private MaterialButton mBlueButton;
    private TextView mToggleErrorText;

    public ObjectiveMatchFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unused")
    public static ObjectiveMatchFragment newInstance() {
        return new ObjectiveMatchFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Save context for later use
        mContext = context;
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
        View v = inflater.inflate(R.layout.fragment_objective_match, container, false);

        // Set up members
        mConstraintLayout = v.findViewById(R.id.layout_obj_main);
        mConstraintLayoutId = mConstraintLayout.getId();
        mChronometer = v.findViewById(R.id.objective_chronometer);
        mActionsListView = v.findViewById(R.id.objective_text_actions);

        // Form members
        mRedButton = v.findViewById(R.id.objective_button_red);
        mBlueButton = v.findViewById(R.id.objective_button_blue);
        mToggleErrorText = v.findViewById(R.id.objective_text_alliance_error);
        mAllianceToggleGroup = new UiUtils.ToggleGroupWrapper(v.findViewById(R.id.objective_togglegroup_alliance));

        // TextInputLayouts go in a wrapper so we can handle their Watchers (avoids scary crashes)
        mTeamNumTextInput =
                new UiUtils.TextInputWrapper(
                        v.findViewById(R.id.objective_text_input_team_number));
        mMatchNumTextInput =
                new UiUtils.TextInputWrapper(
                        v.findViewById(R.id.objective_text_input_match_number));

        return v;
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Use SharedPreferences to get the information for building the UI
        getPrefs();

        // Use the collected information and create the UI!
        generateUI();

        // Set up the ViewModel
        mActionsViewModel = new ViewModelProvider(this).get(ObjectiveScoutingViewModel.class);
        // Subscribe to the ViewModel's changes in the ActionsList.
        subscribeToActions();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Gets and saves the information that determines what Buttons and Spinners to generate.
     */
    private void getPrefs() {
        // Get the SharedPreferences
        SharedPreferences
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // Get all of the spinner contents
        Set<String> set = sharedPreferences.getStringSet(getString(R.string.pref_key_sub_spinner),
                null);

        if (set != null) {
            mSpinnerContents = new String[set.size()][];
            mSpinnerNames = new String[set.size()];
            // Split the spinner contents into arrays
            int i = 0;
            for (String s : set) {
                String[] split = s.split(":");
                mSpinnerContents[i] = split[1].split(",");
                mSpinnerNames[i] = split[0];
                i++;
            }

            hasSpinners = mSpinnerNames.length > 0;
        } else {
            // Preference has never been set; use default options.
            String[] arr = getResources().getStringArray(R.array.sub_spinners);
            mSpinnerContents = new String[arr.length][];
            mSpinnerNames = new String[arr.length];
            // Split the spinner contents into arrays
            int i = 0;
            for (String s : arr) {
                String[] split = s.split(":");
                mSpinnerContents[i] = split[1].split(",");
                mSpinnerNames[i] = split[0];
                i++;
            }

            hasSpinners = true;
        }

        // Get all of the button contents
        set = sharedPreferences.getStringSet(getString(R.string.pref_key_obj_buttons), null);
        if (set != null) {
            mButtonNames = new String[set.size()];
            mButtonAbbreviations = new String[set.size()];

            // Split the button contents into names and abbreviations
            int i = 0;
            for (String s : set) {
                String[] split = s.split(":");
                mButtonNames[i] = split[0];
                mButtonAbbreviations[i] = split[1];
                i++;
            }

            hasButtons = mButtonNames.length > 0;
        } else {
            // Preference has never been set; use default options.
            String[] arr = getResources().getStringArray(R.array.obj_buttons);
            mButtonNames = new String[arr.length];
            mButtonAbbreviations = new String[arr.length];

            // Split the button contents into names and abbreviations
            int i = 0;
            for (String s : arr) {
                String[] split = s.split(":");
                mButtonNames[i] = split[0];
                mButtonAbbreviations[i] = split[1];
                i++;
            }

            hasButtons = true;
        }

    }

    /**
     * Sets an observer on the list of actions so that the TextView can be updated.
     */
    private void subscribeToActions() {
        mActionsViewModel.getLiveData().observe(getViewLifecycleOwner(), (actions) -> {
            // Clear the TextView
            mActionsListView.setText(R.string.action_list_start_prefix);
            // Loop through the actions and add them to the TextView
            if (actions != null) {
                for (Action action : actions) {
                    mActionsListView.append(action.getAbbreviation() + " ");
                }
            }
        });
    }


    /**
     * Starts the live match tracking by starting the chronometer and enabling the buttons.
     */
    private void startMatch() {
        // Start the chronometer
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        for (UiUtils.ButtonInfo buttonInfo : mButtonInfos) {
            buttonInfo.button.setEnabled(true);
        }
        // Start button
        mButtonInfos[0].button.setEnabled(false);
    }

    /**
     * Ends the live match and submits the collected data
     */
    private void endMatch() {
        if (validateNumberTextInput(mTeamNumTextInput,
                getString(R.string.error_no_teamnum),
                getString(R.string.error_notnumber_teamnum),
                getString(R.string.error_toolong_teamnum)) &
            validateNumberTextInput(mMatchNumTextInput,
                    getString(R.string.error_no_matchnum),
                    getString(R.string.error_notnumber_matchnum),
                    getString(R.string.error_toolong_matchnum)) &
            validateToggleGroup()) {

            // Add the Spinner Info
            if (hasSpinners) {
                for (UiUtils.SpinnerInfo spinnerInfo : mSpinnerInfos) {
                    mActionsViewModel.addAction(new Action(spinnerInfo.name,
                            spinnerInfo.contents
                                    [spinnerInfo.spinner.getSelectedItemPosition()]));
                }
            }

            mActionsViewModel.processAndSaveMatch(
                    Objects.requireNonNull(mActionsViewModel.getLiveData().getValue(),
                            "Action list is null"),
                    Integer.parseInt(Objects.requireNonNull(mTeamNumTextInput.getEditText(),
                            "NO TEAM NUM EDIT TEXT").getText().toString()),
                    Integer.parseInt(Objects.requireNonNull(mMatchNumTextInput.getEditText(),
                            "NO MATCH NUM EDIT TEXT").getText().toString()),
                    mAllianceToggleGroup.getToggleGroup().getCheckedButtonId() == mRedButton.getId());


            mButtonInfos[mButtonInfos.length - 1].button.setEnabled(false);
            // Stop the chronometer
            mChronometer.stop();

            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                      .navigate(R.id.action_objectiveMatchFragment_to_matchRecordFragment);

        } else {
            // Toast error
            Toast.makeText(mContext, "Errors found.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Undoes the most recently taken Action
     */
    private void undoAction() {
        mActionsViewModel.removeLastAction();
    }

    /**
     * Generates the UI based on the user preferences
     */
    public void generateUI() {
        // If we don't have buttons, remove their helper views.
        if (!hasButtons) {
            mChronometer.setVisibility(View.GONE);
            mActionsListView.setVisibility(View.GONE);
        }

        // Reusable ConstraintSet
        ConstraintSet constraintSet = new ConstraintSet();

        mButtonInfos = new UiUtils.ButtonInfo[mButtonNames.length];
        if (mButtonInfos.length == 0) mButtonInfos = new UiUtils.ButtonInfo[1];

        if (hasButtons) {
            // Button Infos saved here
            // Set up the start button
            mButtonInfos[0] = setupNewButton(0, constraintSet, mChronometer.getId());
            // Enable the start button
            mButtonInfos[0].button.setEnabled(true);
            for (int i = 1; i < mButtonInfos.length - 1; i++) {
                // Set up the user-defined buttons
                mButtonInfos[i] =
                        setupNewButton(i, constraintSet, mButtonInfos[i - 1].id);
            }
        }

        if (hasSpinners) {
            // Spinner Infos saved here
            mSpinnerInfos = new UiUtils.SpinnerInfo[mSpinnerNames.length];
            // Spinners
            mSpinnerInfos = new UiUtils.SpinnerInfo[mSpinnerNames.length];
            int lastId = hasButtons ? mButtonInfos[mButtonInfos.length - 2].id :
                         mChronometer.getId();
            // Set up first spinner
            mSpinnerInfos[0] = setupNewSpinner(0, constraintSet,
                    lastId);
            // Set up the rest of the spinners
            for (int i = 1; i < mSpinnerNames.length; i++) {
                mSpinnerInfos[i] =
                        setupNewSpinner(i, constraintSet, mSpinnerInfos[i - 1].id);
            }
        }

        // Check which view is the lowest on the screen (most recently created)
        int lastId;
        if (hasSpinners) {
            lastId = mSpinnerInfos[mSpinnerInfos.length - 1].id;
        } else if (hasButtons) {
            lastId = mButtonInfos[mButtonInfos.length - 2].id;
        } else {
            lastId = mAllianceToggleGroup.getToggleGroup().getId();
        }

        // Set up the submit button
        mButtonInfos[mButtonInfos.length - 1] =
                setupNewButton(mButtonInfos.length - 1, constraintSet, lastId);

        // Submit button is enabled if there is no start button
        mButtonInfos[mButtonInfos.length - 1].button.setEnabled(!hasButtons);
    }

    /**
     * Creates and constrains a MaterialButton
     * @param index the index of the button, used for mButtonNames and mButtonAbbreviations
     * @param constraintSet a re-usable ConstraintSet object
     * @param previousId the id of the view that this button should be placed underneath
     * @return a ButtonInfo about the created Button
     */
    private UiUtils.ButtonInfo setupNewButton(int index, ConstraintSet constraintSet, int previousId) {
        // Create the button
        Button button = new MaterialButton(mContext);
        // Generate a unique id for the button
        int buttonId = View.generateViewId();
        button.setId(buttonId);
        // Button is disabled by default
        button.setEnabled(false);
        // Set the text of the button
        button.setText(mButtonNames[index]);
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

        // Return the button info
        return new UiUtils.ButtonInfo(mButtonNames[index], mButtonAbbreviations[index], buttonId,
                button);
    }

    /**
     * Creates and constrains a new Spinner
     * @param index the index of the Spinner, used for mSpinnerNames and mSpinnerContents
     * @param constraintSet a re-usable ConstraintSet object
     * @param previousId the id of the view that this Spinner should be placed below
     * @return a SpinnerInfo about the created Spinner
     */
    private UiUtils.SpinnerInfo setupNewSpinner(int index, ConstraintSet constraintSet, int previousId) {
        // Create the spinner
        Spinner spinner = new Spinner(mContext);
        // Generate a unique id for the spinner
        int spinnerId = View.generateViewId();
        spinner.setId(spinnerId);
        // Create the adapter for the spinner
        spinner.setAdapter(new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_dropdown_item, mSpinnerContents[index]));
        // Add the spinner to the layout
        mConstraintLayout.addView(spinner);

        // Set the constraints for the spinner
        constraintSet.clone(mConstraintLayout);
        // Connect the spinner to the previous spinner
        chainViewsVertically(constraintSet, previousId, spinnerId);
        // Center the spinner horizontally
        centerViewHorizontally(constraintSet, spinnerId);
        // Apply the constraints
        constraintSet.applyTo(mConstraintLayout);

        // Return the spinner info
        return new UiUtils.SpinnerInfo(mSpinnerNames[index], mSpinnerContents[index], spinnerId,
                spinner);

    }

    /**
     * Centers a view horizontally within mConstraintLayout
     * @param constraintSet a re-usable ConstraintSet object
     * @param viewId the id of the view to center
     */
    private void centerViewHorizontally(ConstraintSet constraintSet, int viewId) {
        constraintSet.connect(viewId, ConstraintSet.LEFT, mConstraintLayoutId,
                ConstraintSet.LEFT,
                0);
        constraintSet.connect(viewId, ConstraintSet.RIGHT, mConstraintLayoutId,
                ConstraintSet.RIGHT,
                0);
    }

    /**
     * Places a view below another
     * @param constraintSet a re-usable ConstraintSet object
     * @param topId the id of the upper view
     * @param bottomId the id of the lower view
     */
    private void chainViewsVertically(ConstraintSet constraintSet, int topId, int bottomId) {
        constraintSet.connect(bottomId, ConstraintSet.TOP, topId, ConstraintSet.BOTTOM,
                BUTTON_MARGIN);
    }




    /**
     * Handles all button clicks for this Fragment
     */
    public void onButtonClick(View view) {

        // Check Time Buttons
        if (hasButtons) {

            // Start button
            if (view.getId() == mButtonInfos[0].id) {
                startMatch();
                return;
            }

            // Undo Button
            if (view.getId() == mButtonInfos[mButtonInfos.length - 2].id) {
                undoAction();
                return;
            }

            // Submit Button
            if (view.getId() == mButtonInfos[mButtonInfos.length - 1].id) {
                endMatch();
                return;
            }

            // Time-based data buttons
            for (UiUtils.ButtonInfo bi : mButtonInfos) {
                if (view.getId() == bi.id) {
                    Action a = new Action(bi.abbreviation,
                            SystemClock.elapsedRealtime() - mChronometer.getBase());
                    mActionsViewModel.addAction(a);
                }
            }
        } else {
            // The only option is submit
            endMatch();
        }
    }

    /**
     * Checks that the alliance ToggleGroup is not empty and displays an error if it is.
     *
     * @return true if the group is empty
     */
    private boolean validateToggleGroup() {
        boolean errored = false;
        int checkedButton = mAllianceToggleGroup.getToggleGroup().getCheckedButtonId();

        if (checkedButton == View.NO_ID) {
            // No alliance is selected
            errored = true;

            // Get theme's error color
            TypedValue tv = new TypedValue();
            Resources.Theme theme = mContext.getTheme();
            theme.resolveAttribute(R.attr.colorError, tv, true);
            @ColorInt int color = tv.data;

            // Save the old stroke color
            final ColorStateList oldColor = mRedButton.getStrokeColor();

            // Set the stroke color to the outline color
            mRedButton.setStrokeColor(ColorStateList.valueOf(color));
            mBlueButton.setStrokeColor(ColorStateList.valueOf(color));

            // Display error text
            mToggleErrorText.setText(R.string.error_alliance_missing);
            mToggleErrorText.setVisibility(View.VISIBLE);

            // Reset the ToggleGroup once an item is selected
            mAllianceToggleGroup.setWatcher(
                    new MaterialButtonToggleGroup.OnButtonCheckedListener() {
                        @Override
                        public void onButtonChecked(
                                MaterialButtonToggleGroup group,
                                int checkedId, boolean isChecked) {
                            mRedButton.setStrokeColor(oldColor);
                            mBlueButton.setStrokeColor(oldColor);
                            mToggleErrorText.setText(R.string.empty);
                            mToggleErrorText.setVisibility(View.GONE);
                            mAllianceToggleGroup.removeWatcher(this);
                        }
                    });
        }
        return !errored;
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