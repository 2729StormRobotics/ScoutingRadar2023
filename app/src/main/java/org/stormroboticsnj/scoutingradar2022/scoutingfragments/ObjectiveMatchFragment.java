package org.stormroboticsnj.scoutingradar2022.scoutingfragments;


import static org.stormroboticsnj.scoutingradar2022.database.DataUtils.Action;

import android.content.Context;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import org.stormroboticsnj.scoutingradar2022.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ObjectiveMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ObjectiveMatchFragment extends Fragment {

    private static final String[] BUTTONS = new String[]{
            "Start",
            "Acquire",
            "Upper Hub",
            "Lower Hub",
            "Miss",
            "Start Climb",
            "End Climb",
            "UNDO",
            "SUBMIT"
    };
    private static final boolean HAS_BUTTONS = true;
    private static final String[] BUTTON_ABBREVIATIONS = new String[]{
            "START",
            "AQ",
            "UH",
            "LH",
            "MS",
            "SC",
            "EC",
            "UNDO",
            "SUBMIT"
    };
    private static final String[] SPINNER_NAMES = new String[]{
            "Climb Position"
    };
    private static final String[] SPINNER_ABBREVIATIONS = new String[]{
            "CP"
    };
    private static final boolean HAS_SPINNERS = false;
    private static final String[][] SPINNER_CONTENTS = new String[][]{
            new String[]{"None", "Low", "Mid", "High", "Traversal"}
    };
    private static final String[][] SPINNER_CONTENTS_ABBREVIATIONS = new String[][]{
            new String[]{"0", "L", "M", "H", "T"}
    };
    private static final int BUTTON_MARGIN = 8;
    UiUtils.ButtonInfo[] mButtonInfos;
    UiUtils.SpinnerInfo[] mSpinnerInfos;
    private ObjectiveScoutingViewModel mActionsViewModel;
    private Context mContext;
    // Chronometer
    private Chronometer mChronometer;
    // ConstraintLayout
    private ConstraintLayout mConstraintLayout;
    // Actions List TextView
    private TextView mActionsListView;
    private TextInputLayout mTeamNumTextInput;
    private TextInputLayout mMatchNumTextInput;
    private int mConstraintLayoutId;
    private MaterialButton mRedButton;
    private MaterialButton mBlueButton;
    private TextView mToggleErrorText;
    private MaterialButtonToggleGroup mAllianceToggleGroup;

    public ObjectiveMatchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ObjectiveMatchFragment.
     */

    public static ObjectiveMatchFragment newInstance() {

        return new ObjectiveMatchFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mButtonInfos = new UiUtils.ButtonInfo[BUTTONS.length];


    }

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

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        generateUI();
        mActionsViewModel = new ViewModelProvider(this).get(ObjectiveScoutingViewModel.class);

        subscribeToActions();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_objective_match, container, false);

        mConstraintLayout = v.findViewById(R.id.layout_obj_main);
        mConstraintLayoutId = mConstraintLayout.getId();


        mChronometer = v.findViewById(R.id.objective_chronometer);
        mActionsListView = v.findViewById(R.id.objective_text_actions);

        if (!HAS_BUTTONS) {
            mChronometer.setVisibility(View.GONE);
            mActionsListView.setVisibility(View.GONE);
        }

        mTeamNumTextInput = v.findViewById(R.id.objective_text_input_team_number);
        mMatchNumTextInput = v.findViewById(R.id.objective_text_input_match_number);

        mRedButton = v.findViewById(R.id.objective_button_red);
        mBlueButton = v.findViewById(R.id.objective_button_blue);
        mToggleErrorText = v.findViewById(R.id.objective_text_alliance_error);
        mAllianceToggleGroup = v.findViewById(R.id.objective_togglegroup_alliance);

        return v;
    }

    private void startMatch() {
        // Start the chronometer
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        for (UiUtils.ButtonInfo buttonInfo : mButtonInfos) {
            buttonInfo.button.setEnabled(true);
        }
        mButtonInfos[0].button.setEnabled(false);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

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
            if (HAS_SPINNERS) {
                for (UiUtils.SpinnerInfo spinnerInfo : mSpinnerInfos) {
                    mActionsViewModel.addAction(new Action(spinnerInfo.abbreviation,
                            spinnerInfo.contents_abbreviations
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
                    mAllianceToggleGroup.getCheckedButtonId() == mRedButton.getId());


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

    private void undoAction() {
        mActionsViewModel.removeLastAction();
    }

    public void generateUI() {
        // Reusable ConstraintSet
        ConstraintSet constraintSet = new ConstraintSet();

        if (HAS_BUTTONS) {
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

        if (HAS_SPINNERS) {
            // Spinners
            mSpinnerInfos = new UiUtils.SpinnerInfo[SPINNER_NAMES.length];
            int lastId = HAS_BUTTONS ? mButtonInfos[mButtonInfos.length - 2].id :
                         mChronometer.getId();
            // Set up first spinner
            mSpinnerInfos[0] = setupNewSpinner(0, constraintSet,
                    lastId);
            // Set up the rest of the spinners
            for (int i = 1; i < SPINNER_NAMES.length; i++) {
                mSpinnerInfos[i] =
                        setupNewSpinner(i, constraintSet, mSpinnerInfos[i - 1].id);
            }
        }

        int lastId;
        if (HAS_SPINNERS) {
            lastId = mSpinnerInfos[mSpinnerInfos.length - 1].id;
        } else if (HAS_BUTTONS) {
            lastId = mButtonInfos[mButtonInfos.length - 2].id;
        } else {
            lastId = mChronometer.getId();
        }

        // Set up the submit button
        mButtonInfos[mButtonInfos.length - 1] =
                setupNewButton(mButtonInfos.length - 1, constraintSet, lastId);

        // Submit button is enabled if there is no start button
        mButtonInfos[mButtonInfos.length - 1].button.setEnabled(!HAS_BUTTONS);
    }

    private UiUtils.ButtonInfo setupNewButton(int index, ConstraintSet constraintSet, int previousId) {
        // Create the button
        Button button = new MaterialButton(mContext);
        // Generate a unique id for the button
        int buttonId = View.generateViewId();
        button.setId(buttonId);
        // Button is disabled by default
        button.setEnabled(false);
        // Set the text of the button
        button.setText(BUTTONS[index]);
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
        return new UiUtils.ButtonInfo(BUTTONS[index], BUTTON_ABBREVIATIONS[index], buttonId, button);
    }

    private UiUtils.SpinnerInfo setupNewSpinner(int index, ConstraintSet constraintSet, int previousId) {
        // Create the spinner
        Spinner spinner = new Spinner(mContext);
        // Generate a unique id for the spinner
        int spinnerId = View.generateViewId();
        spinner.setId(spinnerId);
        // Create the adapter for the spinner
        spinner.setAdapter(new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_dropdown_item, SPINNER_CONTENTS[index]));
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
        return new UiUtils.SpinnerInfo(SPINNER_NAMES[index], SPINNER_ABBREVIATIONS[index],
                SPINNER_CONTENTS[index], SPINNER_CONTENTS_ABBREVIATIONS[index], spinnerId,
                spinner);

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
                BUTTON_MARGIN);
    }


    /**
     * Checks that the alliance ToggleGroup is not empty and displays an error if it is.
     *
     * @return true if the group is empty
     */
    private boolean validateToggleGroup() {
        boolean errored = false;
        int checkedButton = mAllianceToggleGroup.getCheckedButtonId();

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
            mAllianceToggleGroup.addOnButtonCheckedListener(
                    new MaterialButtonToggleGroup.OnButtonCheckedListener() {
                        @Override
                        public void onButtonChecked(
                                MaterialButtonToggleGroup group,
                                int checkedId, boolean isChecked) {
                            mRedButton.setStrokeColor(oldColor);
                            mBlueButton.setStrokeColor(oldColor);
                            mToggleErrorText.setText(R.string.empty);
                            mToggleErrorText.setVisibility(View.GONE);
                            mAllianceToggleGroup.removeOnButtonCheckedListener(this);
                        }
                    });
        }
        return !errored;
    }

    public void onButtonClick(View view) {

        if (HAS_BUTTONS) {
            if (view.getId() == mButtonInfos[0].id) {
                startMatch();
                return;
            }

            if (view.getId() == mButtonInfos[mButtonInfos.length - 2].id) {
                undoAction();
                return;
            }

            if (view.getId() == mButtonInfos[mButtonInfos.length - 1].id) {
                endMatch();
                return;
            }

            for (UiUtils.ButtonInfo bi : mButtonInfos) {
                if (view.getId() == bi.id) {
                    Action a = new Action(bi.abbreviation,
                            SystemClock.elapsedRealtime() - mChronometer.getBase());
                    mActionsViewModel.addAction(a);
                }
            }
        } else {
            endMatch();
        }
    }

    /**
     * Checks for and displays errors on a given number-input {@link TextInputLayout} and creates a
     * {@link SmallerTextWatcher} to dismiss the error once it is corrected.
     * <p>
     * The parameters for validation are that the entered input must be 1. Not Empty 2. Digits Only
     * 3. Less than the set max length, if one is set as an attribute of the TextInputLayout.
     *
     * @param textInputLayout TextInputLayout to monitor
     * @param emptyError      The error message to be displayed if this TextInputLayout is empty.
     * @return whether or not an error was found
     */
    public boolean validateNumberTextInput(final TextInputLayout textInputLayout, String emptyError, String notNumberError, String tooLongError) {
        boolean isError = false;
        final EditText editText = textInputLayout.getEditText();

        // This shouldn't happen but TextInputLayout#getEditText() is marked as nullable.
        if (editText == null) {
            return true;
        }
        String input = editText.getText().toString();

        /* Check to make sure the entered text exists, is a number, and is shorter than
           the set max length */
        if (TextUtils.isEmpty(input)) {
            isError = true;
            textInputLayout.setError(emptyError);
        } else if (!TextUtils.isDigitsOnly(input)) {
            isError = true;
            textInputLayout.setError(notNumberError);
        } else if (textInputLayout.getCounterMaxLength() > 0 &&
                   input.length() > textInputLayout.getCounterMaxLength()) {
            isError = true;
            textInputLayout.setError(tooLongError);
        }

        //  If errors were found, add a listener to dismiss the error once it is corrected
        if (isError) {
            editText.addTextChangedListener(new SmallerTextWatcher(textInputLayout) {
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
                        editText.removeTextChangedListener(this);
                    }
                }
            });
        }

        return !isError;
    }

}