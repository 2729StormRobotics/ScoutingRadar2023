package org.stormroboticsnj.scoutingradar2022;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.stormroboticsnj.scoutingradar2022.database.Field;
import org.stormroboticsnj.scoutingradar2022.databinding.FragmentSetupFieldsBinding;
import org.stormroboticsnj.scoutingradar2022.placeholder.PlaceholderContent.PlaceholderItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link org.stormroboticsnj.scoutingradar2022.database.Field}.
 */
public class MyFieldRecyclerViewAdapter extends RecyclerView.Adapter<MyFieldRecyclerViewAdapter.ViewHolder> {

    private final List<Field> mValues;

    public MyFieldRecyclerViewAdapter(List<Field> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(
                FragmentSetupFieldsBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                        false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public Field mItem;

        public ViewHolder(FragmentSetupFieldsBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}