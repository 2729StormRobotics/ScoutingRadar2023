package org.stormrobotics.scoutingradar2022;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.stormrobotics.scoutingradar2022.database.ObjectiveMatchData;
import org.stormrobotics.scoutingradar2022.placeholder.PlaceholderContent.PlaceholderItem;
import org.stormrobotics.scoutingradar2022.databinding.FragmentMatchRecordBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MatchRecordRecyclerViewAdapter extends RecyclerView.Adapter<MatchRecordRecyclerViewAdapter.ViewHolder> {

    private final List<ObjectiveMatchData> mValues;

    public MatchRecordRecyclerViewAdapter(List<ObjectiveMatchData> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(
                FragmentMatchRecordBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                        false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mMatchNumView.setText(mValues.get(position).getMatchNum());
        holder.mTeamNumView.setText(mValues.get(position).getTeamNum());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mMatchNumView;
        public final TextView mTeamNumView;
        public ObjectiveMatchData mItem;

        public ViewHolder(FragmentMatchRecordBinding binding) {
            super(binding.getRoot());
            mMatchNumView = binding.recordTextMatchNumber;
            mTeamNumView = binding.recordTextTeamNumber;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTeamNumView.getText() + "'";
        }
    }
}