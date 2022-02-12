package org.stormroboticsnj.scoutingradar2022.homefragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.stormroboticsnj.scoutingradar2022.R;

import java.util.Objects;

/**
 * A fragment representing a list of Items.
 */
public class MatchRecordFragment extends Fragment {

    NavController mNavController;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MatchRecordFragment() {}

    public static MatchRecordFragment newInstance() {
        return new MatchRecordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_record_list, container, false);

        // Get the recycler view
        RecyclerView recyclerView = view.findViewById(R.id.record_recycler);
        // Create the adapter
        final MatchRecordRecyclerViewAdapter adapter =
                new MatchRecordRecyclerViewAdapter(new MatchRecordRecyclerViewAdapter.RecordDiff());
        // Set up the recycler view with the adapter
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        // Create the ViewModel
        MatchViewModel mMatchViewModel = new ViewModelProvider(this).get(MatchViewModel.class);

        // Connect the viewModel and thus the database with the adapter and thus the recycler view
        mMatchViewModel.getDataList().observe(getViewLifecycleOwner(), adapter::submitList);


        // Setting up SpeedDialView
        SpeedDialView speedDialView = view.findViewById(R.id.record_fab);
        speedDialView.inflate(R.menu.menu_speed_dial);
        speedDialView.setOnActionSelectedListener(this::onActionSelected);

        NavHostFragment navHostFragment =
                (NavHostFragment) requireActivity().getSupportFragmentManager()
                                         .findFragmentById(R.id.nav_host_fragment);
        mNavController = Objects.requireNonNull(navHostFragment, "NO NAV HOST FRAGMENT").getNavController();

        return view;
    }

    public boolean onActionSelected(SpeedDialActionItem actionItem) {

        if (actionItem.getId() == R.id.item_pitscout) {
            mNavController.navigate(R.id.action_matchRecordFragment_to_pitScoutFragment);
        } else if (actionItem.getId() == R.id.item_subjective) {
            mNavController.navigate(R.id.action_matchRecordFragment_to_subjectiveMatchFragment);
        } else if (actionItem.getId() == R.id.item_objective) {
            mNavController.navigate(R.id.action_matchRecordFragment_to_objectiveMatchFragment);
        }

        return false;
    }
}