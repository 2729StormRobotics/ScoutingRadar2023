package org.stormrobotics.scoutingradar2022;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static org.stormrobotics.scoutingradar2022.database.DataProcessor.Action;

import org.stormrobotics.scoutingradar2022.database.AppDatabase;
import org.stormrobotics.scoutingradar2022.database.AppDatabase_Impl;
import org.stormrobotics.scoutingradar2022.database.DataProcessor;
import org.stormrobotics.scoutingradar2022.database.ObjectiveMatchData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ObjectiveMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ObjectiveMatchFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private static final String[] BUTTONS = new String[]{
            "Acquire",
            "Upper Hub",
            "Lower Hub",
            "Miss",
            "Start Climb",
            "End Climb"
    };
    private static final String[] BUTTON_ABBREVIATIONS = new String[]{
            "AQ",
            "UH",
            "LH",
            "MS",
            "SC",
            "EC"
    };
    private static final String[] SPINNER_NAMES = new String[]{
            "Climb Position"
    };
    private static final String[] SPINNER_ABBREVIATIONS = new String[]{
            "CP"
    };
    private static final String[][] SPINNER_CONTENTS = new String[][]{
            new String[]{"None", "Low", "Mid", "High", "Traversal"}
    };
    private static final String[][] SPINNER_CONTENTS_ABBREVIATIONS = new String[][]{
            new String[]{"0", "L", "M", "H", "T"}
    };
    private final List<Action> mActionList = new ArrayList<>();
    ButtonInfo[] mButtonInfos;
    SpinnerInfo[] mSpinnerInfos;
    private Context mContext;
    // Chronometer
    private Chronometer mChronometer;
    // ConstraintLayout
    private ConstraintLayout mConstraintLayout;
    // Actions List TextView
    private TextView mActionsListView;
    private TextInputLayout mTeamNumTextInput;
    private TextInputLayout mMatchNumTextInput;


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
        mButtonInfos = new ButtonInfo[BUTTONS.length + 3];


    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        generateUI();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_objective_match, container, false);

        mConstraintLayout = v.findViewById(R.id.layout_obj_main);
        mChronometer = v.findViewById(R.id.objective_chronometer);
        mActionsListView = v.findViewById(R.id.objective_text_actions);

        mTeamNumTextInput = v.findViewById(R.id.objective_text_input_team_number);
        mMatchNumTextInput = v.findViewById(R.id.objective_text_input_match_number);

        return v;
    }

    private void updateActionsListView() {
        // Clear the TextView
        mActionsListView.setText("START ");
        // Loop through the actions and add them to the TextView
        for (Action action : mActionList) {
            mActionsListView.append(action.getAbbreviation() + " ");
        }

    }

    private void startMatch() {
        // Start the chronometer
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        for (ButtonInfo buttonInfo : mButtonInfos) {
            buttonInfo.button.setEnabled(true);
        }
        mButtonInfos[0].button.setEnabled(false);
        // Update the TextView
        updateActionsListView();

       // AppDatabase.getInstance(mContext).exe

    }

    private void endMatch() {
        mButtonInfos[mButtonInfos.length-1].button.setEnabled(false);
        // Stop the chronometer
        mChronometer.stop();

        // Add the Spinner Info
        for (SpinnerInfo spinnerInfo : mSpinnerInfos) {
            mActionList.add(new Action(spinnerInfo.abbreviation,
                    spinnerInfo.contents_abbreviations
                            [spinnerInfo.spinner.getSelectedItemPosition()]));
        }

        // Update the TextView
        updateActionsListView();

        StringBuilder sb = new StringBuilder();
        for (Action action : mActionList) {
            sb.append(action.getSubAction().equals("N/A") ?
                      String.valueOf(action.getTimeString()) : action.getSubAction())
              .append(" ")
              .append(action.getAbbreviation())
              .append("\n");
        }

        DataProcessor.processObjectiveMatchData(mContext, mActionList,
                Integer.parseInt(mTeamNumTextInput.getEditText().getText().toString()),
                Integer.parseInt(mMatchNumTextInput.getEditText().getText().toString()), true);

        // Make pop-up with result
        new AlertDialog.Builder(mContext).setTitle("Result").setMessage(sb.toString()).show();
    }

    private void undoAction() {
        if (mActionList.size() > 1) {
            mActionList.remove(mActionList.size() - 1);
        }
        updateActionsListView();
    }

    public void generateUI() {
        ConstraintSet constraintSet = new ConstraintSet();

        Button start = new MaterialButton(mContext);
        int startId = View.generateViewId();
        start.setId(startId);
        mButtonInfos[0] = new ButtonInfo(getString(R.string.button_start),
                getString(R.string.abbreviation_start), startId, start);
        start.setText(R.string.button_start);
        start.setOnClickListener(this);
        mConstraintLayout.addView(start);
        constraintSet.clone(mConstraintLayout);
        constraintSet.connect(start.getId(), ConstraintSet.TOP, mChronometer.getId(),
                ConstraintSet.BOTTOM,
                0);
        constraintSet.connect(start.getId(), ConstraintSet.LEFT, mConstraintLayout.getId(),
                ConstraintSet.LEFT,
                0);
        constraintSet.connect(start.getId(), ConstraintSet.RIGHT, mConstraintLayout.getId(),
                ConstraintSet.RIGHT, 0);
        constraintSet.applyTo(mConstraintLayout);


        for (int i = 1; i < mButtonInfos.length - 2; i++) {
            Button btn = new MaterialButton(mContext);
            int buttonId = View.generateViewId();
            btn.setId(buttonId);
            btn.setEnabled(false);
            mButtonInfos[i] = new ButtonInfo(BUTTONS[i - 1], BUTTON_ABBREVIATIONS[i - 1],
                    buttonId, btn);
            btn.setText(BUTTONS[i - 1]);
            btn.setOnClickListener(this);
            mConstraintLayout.addView(btn);
            constraintSet.clone(mConstraintLayout);
            // Chain the buttons together
            constraintSet.connect(buttonId, ConstraintSet.TOP, mButtonInfos[i - 1].id,
                    ConstraintSet.BOTTOM, 0);
            constraintSet.connect(mButtonInfos[i - 1].id, ConstraintSet.BOTTOM, buttonId,
                    ConstraintSet.TOP, 0);
            // Center the button horizontally
            constraintSet.connect(buttonId, ConstraintSet.LEFT, mConstraintLayout.getId(),
                    ConstraintSet.LEFT,
                    0);
            constraintSet.connect(buttonId, ConstraintSet.RIGHT, mConstraintLayout.getId(),
                    ConstraintSet.RIGHT, 0);
            constraintSet.applyTo(mConstraintLayout);

        }

        Button undo = new MaterialButton(mContext);
        int undoId = View.generateViewId();
        undo.setId(undoId);
        undo.setEnabled(false);
        mButtonInfos[mButtonInfos.length - 2] =
                new ButtonInfo(getString(R.string.button_undo),
                        getString(R.string.abbreviation_undo), undoId, undo);
        undo.setText(getString(R.string.button_undo));
        undo.setOnClickListener(this);
        mConstraintLayout.addView(undo);
        constraintSet.clone(mConstraintLayout);
        constraintSet.connect(undoId, ConstraintSet.TOP, mButtonInfos[mButtonInfos.length - 3].id,
                ConstraintSet.BOTTOM, 0);
        constraintSet.connect(mButtonInfos[mButtonInfos.length - 3].id, ConstraintSet.BOTTOM,
                undoId,
                ConstraintSet.TOP, 0);
        constraintSet.connect(undo.getId(), ConstraintSet.LEFT, mConstraintLayout.getId(),
                ConstraintSet.LEFT,
                0);
        constraintSet.connect(undo.getId(), ConstraintSet.RIGHT, mConstraintLayout.getId(),
                ConstraintSet.RIGHT,
                0);
        constraintSet.applyTo(mConstraintLayout);

        mSpinnerInfos = new SpinnerInfo[SPINNER_NAMES.length];

        for (int i = 0; i < SPINNER_NAMES.length; i++) {
            Spinner spnr = new Spinner(mContext);
            int spinnerId = View.generateViewId();
            spnr.setId(spinnerId);
            mSpinnerInfos[i] = new SpinnerInfo(SPINNER_NAMES[i], SPINNER_ABBREVIATIONS[i],
                    SPINNER_CONTENTS[i], SPINNER_CONTENTS_ABBREVIATIONS[i], spinnerId, spnr);
            spnr.setOnItemSelectedListener(this);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(mContext,
                    android.R.layout.simple_spinner_dropdown_item, SPINNER_CONTENTS[i]);
            spnr.setAdapter(spinnerArrayAdapter);

            mConstraintLayout.addView(spnr);
            constraintSet.clone(mConstraintLayout);
            constraintSet.connect(spinnerId, ConstraintSet.TOP,
                    i == 0 ? undoId : mSpinnerInfos[i - 1].id, ConstraintSet.BOTTOM, 0);
            constraintSet.connect(spinnerId, ConstraintSet.LEFT, mConstraintLayout.getId(),
                    ConstraintSet.LEFT,
                    0);
            constraintSet.connect(spinnerId, ConstraintSet.RIGHT, mConstraintLayout.getId(),
                    ConstraintSet.RIGHT,
                    0);
            constraintSet.applyTo(mConstraintLayout);
        }

        Button submit = new MaterialButton(mContext);
        int submitId = View.generateViewId();
        submit.setId(submitId);
        submit.setEnabled(false);
        mButtonInfos[mButtonInfos.length - 1] = new ButtonInfo(getString(R.string.button_submit),
                getString(R.string.abbreviation_submit), submitId, submit);
        submit.setText(getString(R.string.button_submit));
        submit.setOnClickListener(this);

        mConstraintLayout.addView(submit);
        constraintSet.clone(mConstraintLayout);
        constraintSet.connect(submitId, ConstraintSet.TOP,
                mSpinnerInfos[mSpinnerInfos.length - 1].id, ConstraintSet.BOTTOM, 0);
        constraintSet.connect(submitId, ConstraintSet.LEFT, mConstraintLayout.getId(),
                ConstraintSet.LEFT,
                0);
        constraintSet.connect(submitId, ConstraintSet.RIGHT, mConstraintLayout.getId(),
                ConstraintSet.RIGHT,
                0);
        constraintSet.applyTo(mConstraintLayout);

    }

    @Override
    public void onClick(View view) {

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

        for (ButtonInfo bi : mButtonInfos) {

            if (view.getId() == bi.id) {
                Action a = new Action(bi.abbreviation,
                        SystemClock.elapsedRealtime() - mChronometer.getBase());
                mActionList.add(a);
                updateActionsListView();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private static class ButtonInfo {
        String name;
        String abbreviation;
        int id;
        Button button;

        public ButtonInfo(String name, String abbreviation, int id, Button button) {
            this.name = name;
            this.abbreviation = abbreviation;
            this.id = id;
            this.button = button;
        }


    }

    private static class SpinnerInfo {
        String name;
        String abbreviation;
        String[] contents;
        String[] contents_abbreviations;
        int id;
        Spinner spinner;

        public SpinnerInfo(
                String name, String abbreviation, String[] contents,
                String[] contents_abbreviations, int id, Spinner spinner) {
            this.name = name;
            this.abbreviation = abbreviation;
            this.contents = contents;
            this.contents_abbreviations = contents_abbreviations;
            this.id = id;
            this.spinner = spinner;
        }

    }
}