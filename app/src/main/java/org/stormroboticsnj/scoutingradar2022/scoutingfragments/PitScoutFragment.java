package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import static org.stormroboticsnj.scoutingradar2022.scoutingfragments.UiUtils.SpinnerInfo;

import android.content.Context;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.stormroboticsnj.scoutingradar2022.R;
import org.stormroboticsnj.scoutingradar2022.database.DataUtils;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PitScoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PitScoutFragment extends Fragment {

    private static final String[] SPINNER_NAMES = new String[]{
            "Driver Age",
            "Drivetrain"
    };
    private static final String[] SPINNER_ABBREVIATIONS = new String[]{
            "DrA",
            "Drt",
            };
    private static final String[][] SPINNER_CONTENTS = new String[][]{
            new String[]{"1", "2", "3", "4", "5"},
            new String[]{
                    "Tank",
                    "West Coast",
                    "Swerve",
                    "Mecanum",
                    "Other",
                    }
    };
    private static final int BUTTON_MARGIN = 8;
    private SpinnerInfo[] mSpinnerInfos;
    private PitScoutViewModel mActionsViewModel;
    private Context mContext;
    // ConstraintLayout
    private ConstraintLayout mConstraintLayout;
    // Actions List TextView
    private TextInputLayout mTeamNumTextInput;
    private TextInputLayout mNotesTextInput;
    private int mConstraintLayoutId;
    private Button mSubmitButton;


    public PitScoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PitScoutFragment.
     */
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

        mTeamNumTextInput = v.findViewById(R.id.pit_text_input_team_num);
        mNotesTextInput = v.findViewById(R.id.pit_text_input_notes);

        return v;
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        generateUI();
        mActionsViewModel = new ViewModelProvider(this).get(PitScoutViewModel.class);
    }

    public void generateUI() {
        // Reusable ConstraintSet
        ConstraintSet constraintSet = new ConstraintSet();
        // Spinners
        mSpinnerInfos = new SpinnerInfo[SPINNER_NAMES.length];
        // Set up first spinner
        mSpinnerInfos[0] = mSpinnerInfos[0] =
                setupNewSpinner(0, constraintSet, mNotesTextInput.getId());
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
                mActionsViewModel.addAction(new DataUtils.Action(spinnerInfo.abbreviation,
                        spinnerInfo.contents[spinnerInfo.spinner.getSelectedItemPosition()]));
            }

            mActionsViewModel.processAndSaveMatch(
                    Integer.parseInt(Objects.requireNonNull(mTeamNumTextInput.getEditText(),
                            "NO TEAM NUM EDIT TEXT").getText().toString()),
                    Objects.requireNonNull(mNotesTextInput.getEditText(), "NO NOTES EDIT TEXT")
                           .getText()
                           .toString());

            //set submit to disable
            mSubmitButton.setEnabled(false);
//            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
//                      .navigate(R.id.action_subjectiveMatchFragment_to_matchRecordFragment);

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
        return new SpinnerInfo(SPINNER_NAMES[index], SPINNER_ABBREVIATIONS[index],
                SPINNER_CONTENTS[index], SPINNER_CONTENTS[index], spinnerId, spinner);

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
     * Checks for and displays errors on a given number-input {@link TextInputLayout} and creates a
     * {@link org.stormroboticsnj.scoutingradar2022.scoutingfragments.SmallerTextWatcher} to dismiss the error once it is corrected.
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
            editText.addTextChangedListener(
                    new org.stormroboticsnj.scoutingradar2022.scoutingfragments.SmallerTextWatcher(
                            textInputLayout) {
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