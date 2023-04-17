package org.stormroboticsnj.scoutingradar2022.homefragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.stormroboticsnj.scoutingradar2022.R;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitScoutData;
import org.stormroboticsnj.scoutingradar2022.databinding.FragmentMatchRecordBinding;

/**
 * {@link RecyclerView.Adapter} that can display an {@link PitScoutData}.
 */
public class PitRecordRecyclerViewAdapter extends ListAdapter<PitScoutData, PitRecordRecyclerViewAdapter.PitRecordViewHolder> {
    protected PitRecordRecyclerViewAdapter(
            @NonNull
            DiffUtil.ItemCallback<PitScoutData> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public PitRecordViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new PitRecordViewHolder(
                FragmentMatchRecordBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                        false));
    }

    @Override
    public void onBindViewHolder(
            @NonNull PitRecordViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    public static class PitRecordViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTeamNumView;
        PitScoutData mItem;

        public PitRecordViewHolder(FragmentMatchRecordBinding binding) {
            super(binding.getRoot());
            mTeamNumView = binding.recordTextTeamNumber;
        }

        public void bind(PitScoutData item) {
            mItem = item;
            mTeamNumView.setText(mTeamNumView.getContext().getResources().getString(R.string.record_text_team, item.getTeamNum()));
        }

        @Override
        @NonNull
        public String toString() {
            return super.toString() + " '" + mTeamNumView.getText() + "'";
        }
    }

    public static class RecordDiff extends DiffUtil.ItemCallback<PitScoutData> {
        @Override
        public boolean areItemsTheSame(
                @NonNull PitScoutData oldItem, @NonNull PitScoutData newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull PitScoutData oldItem, @NonNull PitScoutData newItem) {
            return (oldItem.getTeamNum() == newItem.getTeamNum());
        }
    }
}