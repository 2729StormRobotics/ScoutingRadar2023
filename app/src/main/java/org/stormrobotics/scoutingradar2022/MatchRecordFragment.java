package org.stormrobotics.scoutingradar2022;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBindings;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

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
    public void onCreateOptionsMenu(
            @NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_top_app_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.item_settings) {
            mNavController.navigate(R.id.action_matchRecordFragment_to_settingsFragment);
            return true;
        } else if (itemId == R.id.item_import_data) {
            mNavController.navigate(R.id.action_matchRecordFragment_to_importDataFragment);
            return true;
        } else if (itemId == R.id.item_export_data) {
            mNavController.navigate(R.id.action_matchRecordFragment_to_exportDataFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);

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
                (NavHostFragment) getActivity().getSupportFragmentManager()
                                               .findFragmentById(R.id.nav_host_fragment);
        mNavController = navHostFragment.getNavController();

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