package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.stormroboticsnj.scoutingradar2022.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubjectiveMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubjectiveMatchFragment extends Fragment {

    private static final String[] SPINNERS_NAMES = new String[]{
            "Driver Skill",
            "Defense Skill"
    };
    private static final boolean HAS_SPINNERS = true;
    private static final String[] SPINNER_RATING = new String[]{
            "0",
            "1",
            "2",
            "3",
            "4",
            "5"
    };

    private Context mContext;

    public SubjectiveMatchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ObjectiveMatchFragment.
     */

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
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subjective_match, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        generateUI();
        SubjectiveScoutingViewModel mActionsViewModel =
                new ViewModelProvider(this).get(SubjectiveScoutingViewModel.class);

        subscribeToActions();
    }

    private void subscribeToActions() {
    }
}

//    private void generateUI() {
//        if (HAS_SPINNERS) {
//            // Spinners
//            mSpinnerInfos = new ObjectiveMatchFragment.SpinnerInfo[SPINNER_NAMES.length];
//            int lastId = HAS_BUTTONS ? mButtonInfos[mButtonInfos.length - 2].id :
//                         mChronometer.getId();
//            // Set up first spinner
//            mSpinnerInfos[0] = setupNewSpinner(0, constraintSet,
//                    lastId);
//            // Set up the rest of the spinners
//            for (int i = 1; i < SPINNER_NAMES.length; i++) {
//                mSpinnerInfos[i] =
//                        setupNewSpinner(i, constraintSet, mSpinnerInfos[i - 1].id);
//            }
//        }
//
//        int lastId;
//        if (HAS_SPINNERS) {
//            lastId = mSpinnerInfos[mSpinnerInfos.length - 1].id;
//        } else if (HAS_BUTTONS) {
//            lastId = mButtonInfos[mButtonInfos.length - 2].id;
//        } else {
//            lastId = mChronometer.getId();
//        }
//    }