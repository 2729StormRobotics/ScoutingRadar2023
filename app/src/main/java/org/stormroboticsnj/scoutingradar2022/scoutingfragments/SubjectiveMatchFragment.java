package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import static org.stormroboticsnj.scoutingradar2022.scoutingfragments.UiUtils.SpinnerInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
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
import org.stormroboticsnj.scoutingradar2022.database.DataUtils;

import java.util.Objects;
import java.util.Set;


public class SubjectiveMatchFragment extends Fragment {


    private static final int BUTTON_MARGIN = 8;
    private SpinnerInfo[] mSpinnerInfos;
    private SubjectiveScoutingViewModel mActionsViewModel;
    private Context mContext;
    // ConstraintLayout
    private ConstraintLayout mConstraintLayout;
    // Actions List TextView
    private UiUtils.TextInputWrapper mTeamNumTextInput;
    private UiUtils.TextInputWrapper mMatchNumTextInput;
    private int mConstraintLayoutId;
    private MaterialButton mRedButton;
    private MaterialButton mBlueButton;
    private Button mSubmitButton;
    private TextView mToggleErrorText;
    private MaterialButtonToggleGroup mAllianceToggleGroup;

    private String[] SPINNER_NAMES;
    private String[][] SPINNER_CONTENTS;

    public SubjectiveMatchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ObjectiveMatchFragment.
     */

    @SuppressWarnings("unused")
    public static SubjectiveMatchFragment newInstance() {
        return new SubjectiveMatchFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the SharedPreferences
        SharedPreferences
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // Get all of the spinner contents
        Set<String> set = sharedPreferences.getStringSet(getString(R.string.pref_key_sub_spinner),
                null);

        if (set != null) {
            SPINNER_CONTENTS = new String[set.size()][];
            SPINNER_NAMES = new String[set.size()];
            // Split the spinner contents into arrays
            int i = 0;
            for (String s : set) {
                String[] split = s.split(":");
                SPINNER_CONTENTS[i] = split[1].split(",");
                SPINNER_NAMES[i] = split[0];
                i++;
            }
        } else {
            String[] arr = getResources().getStringArray(R.array.sub_spinners);
            SPINNER_CONTENTS = new String[arr.length][];
            SPINNER_NAMES = new String[arr.length];
            // Split the spinner contents into arrays
            int i = 0;
            for (String s : arr) {
                String[] split = s.split(":");
                SPINNER_CONTENTS[i] = split[1].split(",");
                SPINNER_NAMES[i] = split[0];
                i++;
            }
        }


        generateUI();
        mActionsViewModel = new ViewModelProvider(this).get(SubjectiveScoutingViewModel.class);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_subjective_match, container, false);

        mConstraintLayout = v.findViewById(R.id.layout_sub_main);
        mConstraintLayoutId = mConstraintLayout.getId();

        mTeamNumTextInput = new UiUtils.TextInputWrapper(
                v.findViewById(R.id.subjective_text_input_team_number));
        mMatchNumTextInput = new UiUtils.TextInputWrapper(
                v.findViewById(R.id.subjective_text_input_match_number));

        mRedButton = v.findViewById(R.id.subjective_button_red);
        mBlueButton = v.findViewById(R.id.subjective_button_blue);
        //need to do submit Button!!
        mToggleErrorText = v.findViewById(R.id.subjective_text_alliance_error);
        mAllianceToggleGroup = v.findViewById(R.id.subjective_togglegroup_alliance);

        return v;
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
            for (SpinnerInfo spinnerInfo : mSpinnerInfos) {
                mActionsViewModel.addAction(new DataUtils.Action(spinnerInfo.name,
                        spinnerInfo.contents[spinnerInfo.spinner.getSelectedItemPosition()]));
            }

            mActionsViewModel.processAndSaveMatch(
                    Integer.parseInt(Objects.requireNonNull(mTeamNumTextInput.getEditText(),
                            "NO TEAM NUM EDIT TEXT").getText().toString()),
                    Integer.parseInt(Objects.requireNonNull(mMatchNumTextInput.getEditText(),
                            "NO MATCH NUM EDIT TEXT").getText().toString()),
                    mAllianceToggleGroup.getCheckedButtonId() == mRedButton.getId());


            //set submit to disable
            mSubmitButton.setEnabled(false);
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                      .navigate(R.id.action_subjectiveMatchFragment_to_matchRecordFragment);

        } else {
            // Toast error
            Toast.makeText(mContext, "Errors found.", Toast.LENGTH_SHORT).show();
        }
    }


    public void generateUI() {
        // Reusable ConstraintSet
        ConstraintSet constraintSet = new ConstraintSet();
        // Spinners
        mSpinnerInfos = new SpinnerInfo[SPINNER_NAMES.length];
        // Set up first spinner
        mSpinnerInfos[0] = setupNewSpinner(0, constraintSet,
                mAllianceToggleGroup.getId());
        // Set up the rest of the spinners
        for (int i = 1; i < SPINNER_NAMES.length; i++) {
            mSpinnerInfos[i] =
                    setupNewSpinner(i, constraintSet, mSpinnerInfos[i - 1].id);
        }

        mSubmitButton = setupSubmitButton(constraintSet,
                mSpinnerInfos[mSpinnerInfos.length - 1].id);

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
        // Create the adapter for the spinner
        spinner.setAdapter(new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_dropdown_item, SPINNER_CONTENTS[index]));
        // Add the spinner to the layout
        mConstraintLayout.addView(spinner);
        // Add the textview to the layout
        mConstraintLayout.addView(textView);

        // Set the constraints for the spinner
        constraintSet.clone(mConstraintLayout);
        // Connect the spinner to the previous spinner
        chainViewsVertically(constraintSet, previousId, spinnerId);

        constraintSet.connect(textId, ConstraintSet.LEFT, mConstraintLayoutId, ConstraintSet.LEFT,
                0);
        constraintSet.connect(textId, ConstraintSet.RIGHT, spinnerId, ConstraintSet.LEFT, 8);
        constraintSet.connect(spinnerId, ConstraintSet.LEFT, textId, ConstraintSet.RIGHT, 0);
        constraintSet.connect(spinnerId, ConstraintSet.RIGHT, mConstraintLayoutId,
                ConstraintSet.RIGHT, 0);
        constraintSet.connect(textId, ConstraintSet.TOP, spinnerId, ConstraintSet.TOP);
        constraintSet.connect(textId, ConstraintSet.BOTTOM, spinnerId, ConstraintSet.BOTTOM);

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

        if (view.getId() == mSubmitButton.getId()) {
            endMatch();
        }

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