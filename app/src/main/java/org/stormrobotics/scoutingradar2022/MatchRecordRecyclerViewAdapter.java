package org.stormrobotics.scoutingradar2022;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.stormrobotics.scoutingradar2022.database.ObjectiveMatchData;
import org.stormrobotics.scoutingradar2022.placeholder.PlaceholderContent.PlaceholderItem;
import org.stormrobotics.scoutingradar2022.databinding.FragmentMatchRecordBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MatchRecordRecyclerViewAdapter extends ListAdapter<ObjectiveMatchData, MatchRecordRecyclerViewAdapter.MatchRecordViewHolder> {
    protected MatchRecordRecyclerViewAdapter(
            @NonNull
                    DiffUtil.ItemCallback<ObjectiveMatchData> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public MatchRecordViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new MatchRecordViewHolder(
                FragmentMatchRecordBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                        false));
    }

    @Override
    public void onBindViewHolder(
            @NonNull MatchRecordViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    public static class MatchRecordViewHolder extends RecyclerView.ViewHolder {
        public final TextView mMatchNumView;
        public final TextView mTeamNumView;
        ObjectiveMatchData mItem;

        public MatchRecordViewHolder(FragmentMatchRecordBinding binding) {
            super(binding.getRoot());
            mMatchNumView = binding.recordTextMatchNumber;
            mTeamNumView = binding.recordTextTeamNumber;
        }

        public void bind(ObjectiveMatchData item) {
            mItem = item;
            mMatchNumView.setText(item.getMatchNum());
            mTeamNumView.setText(item.getTeamNum());
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTeamNumView.getText() + "'";
        }
    }

    public static class RecordDiff extends DiffUtil.ItemCallback<ObjectiveMatchData> {
        @Override
        public boolean areItemsTheSame(
                @NonNull ObjectiveMatchData oldItem, @NonNull ObjectiveMatchData newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull ObjectiveMatchData oldItem, @NonNull ObjectiveMatchData newItem) {
            return false;
        }
    }
}