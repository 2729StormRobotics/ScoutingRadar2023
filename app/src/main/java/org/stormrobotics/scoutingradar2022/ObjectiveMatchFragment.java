package org.stormrobotics.scoutingradar2022;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ObjectiveMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ObjectiveMatchFragment extends Fragment implements View.OnClickListener {

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
    private final List<Action> mActionList = new ArrayList<>();
    ButtonInfo[] mButtonInfos;
    private Context mContext;
    // Chronometer
    private Chronometer mChronometer;
    // ConstraintLayout
    private ConstraintLayout mConstraintLayout;
    // Actions List TextView
    private TextView mActionsListView;

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
        mButtonInfos = new ButtonInfo[BUTTONS.length + 2];


    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        generateButtons();
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

        return v;
    }

    private void updateActionsListView() {
        // Clear the TextView
        mActionsListView.setText("");
        // Loop through the actions and add them to the TextView
        for (Action action : mActionList) {
            mActionsListView.append(action.getAbbreviation() + " ");
        }
    }

    private void startMatch() {
        // Start the chronometer
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        // Add the START action to the actions list
        mActionList.add(new Action(mButtonInfos[0].abbreviation, 0L));

        // Update the TextView
        updateActionsListView();
    }

    private void endMatch() {
        // Stop the chronometer
        mChronometer.stop();

        // Add the STOP action to the actions list
        mActionList.add(new Action(mButtonInfos[mButtonInfos.length - 1].abbreviation,
                SystemClock.elapsedRealtime() - mChronometer.getBase()));

        // Update the TextView
        updateActionsListView();

        StringBuilder sb = new StringBuilder();
        for (Action action : mActionList) {
            sb.append(action.getTimeString()).append(" ").append(action.getAbbreviation()).append("\n");
        }

        // Make pop-up with result
        new AlertDialog.Builder(mContext).setTitle("Result").setMessage(sb.toString()).show();

    }

    public void generateButtons() {
        ConstraintSet constraintSet = new ConstraintSet();

        Button start = new Button(mContext);
        int startId = View.generateViewId();
        start.setId(startId);
        mButtonInfos[0] = new ButtonInfo(getString(R.string.button_start),
                getString(R.string.abbreviation_start), startId);
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


        for (int i = 1; i < mButtonInfos.length - 1; i++) {
            Button btn = new Button(mContext);
            int buttonId = View.generateViewId();
            btn.setId(buttonId);
            mButtonInfos[i] = new ButtonInfo(BUTTONS[i - 1], BUTTON_ABBREVIATIONS[i - 1], buttonId);
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

        Button end = new Button(mContext);
        int endId = View.generateViewId();
        end.setId(endId);
        mButtonInfos[mButtonInfos.length - 1] =
                new ButtonInfo(getString(R.string.button_end), getString(R.string.abbreviation_end),
                        endId);
        end.setText(R.string.button_end);
        end.setOnClickListener(this);
        mConstraintLayout.addView(end);
        constraintSet.clone(mConstraintLayout);
        constraintSet.connect(endId, ConstraintSet.TOP, mButtonInfos[mButtonInfos.length - 2].id,
                ConstraintSet.BOTTOM, 0);
        constraintSet.connect(mButtonInfos[mButtonInfos.length - 2].id, ConstraintSet.BOTTOM, endId,
                ConstraintSet.TOP, 0);
        constraintSet.connect(end.getId(), ConstraintSet.LEFT, mConstraintLayout.getId(),
                ConstraintSet.LEFT,
                0);
        constraintSet.connect(end.getId(), ConstraintSet.RIGHT, mConstraintLayout.getId(),
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


    private static class ButtonInfo {
        String name;
        String abbreviation;
        int id;

        public ButtonInfo(String name, String abbreviation, int id) {
            this.name = name;
            this.abbreviation = abbreviation;
            this.id = id;
        }
    }

    private static class Action {
        private final String abbreviation;
        private final long time;


        public Action(String action, long time) {
            this.abbreviation = action;
            this.time = time;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public int getTimeSeconds() {
            return (int) this.time / 1000;
        }

        public long getTime() {
            return time;
        }

        public String getTimeString() {
            return String.format(Locale.getDefault(), "%02d:%02d", time / 60000,
                    (time % 60000) / 1000);
        }


    }
}