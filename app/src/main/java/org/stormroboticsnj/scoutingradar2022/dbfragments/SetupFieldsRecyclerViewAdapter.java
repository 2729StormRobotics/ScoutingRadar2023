package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.stormroboticsnj.scoutingradar2022.R;
import org.stormroboticsnj.scoutingradar2022.database.field.Field;
import org.stormroboticsnj.scoutingradar2022.databinding.FragmentSetupFieldsBinding;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Field}.
 */
public class SetupFieldsRecyclerViewAdapter extends ListAdapter<Field, SetupFieldsRecyclerViewAdapter.ViewHolder> {

    protected SetupFieldsRecyclerViewAdapter(
            @NonNull
                    DiffUtil.ItemCallback<Field> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                FragmentSetupFieldsBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                        false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mCategoryView;
        public final TextView mNameView;
        public final TextView mAbbreviationView;

        public Field mItem;

        public ViewHolder(FragmentSetupFieldsBinding binding) {
            super(binding.getRoot());
            mCategoryView = binding.fieldTextCategory;
            mNameView = binding.fieldTextName;
            mAbbreviationView = binding.fieldTextAbbreviation;
        }

        public void bind(Field item) {
            mItem = item;
            mCategoryView.setText(mCategoryView.getContext()
                                               .getString(R.string.field_text_cat,
                                                       item.getCategoryString()));
            mNameView.setText(
                    mNameView.getContext().getString(R.string.field_text_name, item.getName()));
            mAbbreviationView.setText(mAbbreviationView.getContext()
                                                       .getString(R.string.field_text_abbr,
                                                               item.getAbbreviation()));
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAbbreviationView.getText() + "'";
        }
    }

    public static class RecordDiff extends DiffUtil.ItemCallback<Field> {
        @Override
        public boolean areItemsTheSame(
                @NonNull Field oldItem, @NonNull Field newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull Field oldItem, @NonNull Field newItem) {
            return oldItem.equals(newItem);
        }
    }
}