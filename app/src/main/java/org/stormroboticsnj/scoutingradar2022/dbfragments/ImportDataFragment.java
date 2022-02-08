package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.stormroboticsnj.scoutingradar2022.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImportDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImportDataFragment extends Fragment {

    public ImportDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     @return A new instance of fragment ImportDataFragment.
     */
    public static ImportDataFragment newInstance() {
        return new ImportDataFragment();
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
        return inflater.inflate(R.layout.fragment_import_data, container, false);
    }
}