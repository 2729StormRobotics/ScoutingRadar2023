package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.stormroboticsnj.scoutingradar2022.R;

/**
 * A fragment representing a list of Items.
 */
public class SetupFieldsFragment extends Fragment {

    private SetupFieldsViewModel mViewModel;

    public SetupFieldsFragment() {
    }

    @SuppressWarnings("unused")
    public static SetupFieldsFragment newInstance(int columnCount) {
        return new SetupFieldsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_fields_list, container, false);

        // Get the recycler view
        RecyclerView recyclerView = view.findViewById(R.id.field_recycler);

        // Create the adapter
        final SetupFieldsRecyclerViewAdapter adapter =
                new SetupFieldsRecyclerViewAdapter(new SetupFieldsRecyclerViewAdapter.RecordDiff());
        // Setup the recycler view with the adapter
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Create the ViewModel
        mViewModel = new ViewModelProvider(this).get(SetupFieldsViewModel.class);

        // Connect the viewModel and thus the database with the adapter and thus the recycler view
        mViewModel.getDataList().observe(getViewLifecycleOwner(), adapter::submitList);

        // Set up the add button
        view.findViewById(R.id.field_fab).setOnClickListener(view1 -> {

        });
        return view;
    }
}