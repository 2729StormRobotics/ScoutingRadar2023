package org.stormrobotics.scoutingradar2022;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment representing a list of Items.
 */
public class MatchRecordFragment extends Fragment {

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
        final MatchRecordRecyclerViewAdapter adapter = new MatchRecordRecyclerViewAdapter(new MatchRecordRecyclerViewAdapter.RecordDiff());
        // Set up the recycler view with the adapter
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        // Create the ViewModel
        MatchViewModel mMatchViewModel = new ViewModelProvider(this).get(MatchViewModel.class);

        // Connect the viewModel and thus the database with the adapter and thus the recycler view
        mMatchViewModel.getDataList().observe(getViewLifecycleOwner(), adapter::submitList);
        return view;
    }
}