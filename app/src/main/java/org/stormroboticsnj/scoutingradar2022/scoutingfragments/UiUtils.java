package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class UiUtils {

    static class ButtonInfo {
        String name;
        String abbreviation;
        int id;
        Button button;

        public ButtonInfo(String name, String abbreviation, int id, Button button) {
            this.name = name;
            this.abbreviation = abbreviation;
            this.id = id;
            this.button = button;
        }
    }

    static class SpinnerInfo {
        String name;
        String[] contents;
        int id;
        Spinner spinner;

        public SpinnerInfo(
                String name,  String[] contents, int id, Spinner spinner) {
            this.name = name;
            this.contents = contents;
            this.id = id;
            this.spinner = spinner;
        }

    }

    /**
     * Wrapper Class for TextInputLayout that allows its EditText to have only one TextWatcher.
     */
    public static class TextInputWrapper {

        private final TextInputLayout mTextInputLayout;
        @NonNull
        private final EditText mEditText;
        private boolean hasTextWatcher = false;

        public TextInputWrapper(@NonNull TextInputLayout layout) {
            this.mTextInputLayout = layout;
            this.mEditText = Objects.requireNonNull(mTextInputLayout.getEditText(),
                    "TextInputLayout has no EditText!");
        }

        public void setTextWatcher(TextWatcher textWatcher) {
            if (!hasTextWatcher) {
                mEditText.addTextChangedListener(textWatcher);
                hasTextWatcher = true;
            }
        }

        public void removeTextWatcher(TextWatcher textWatcher) {
            if (hasTextWatcher) {
                mEditText.removeTextChangedListener(textWatcher);
                hasTextWatcher = false;
            }
        }

        public TextInputLayout getInputLayout() {
            return mTextInputLayout;
        }

        @NonNull
        public EditText getEditText() {
            return mEditText;
        }
    }

    public static class ToggleGroupWrapper {
        private final MaterialButtonToggleGroup toggleGroup;
        private boolean hasWatcher = false;

        public ToggleGroupWrapper(@NonNull MaterialButtonToggleGroup toggleGroup) {
            this.toggleGroup = toggleGroup;
        }

        public MaterialButtonToggleGroup getToggleGroup() {
            return toggleGroup;
        }

        public void setWatcher(MaterialButtonToggleGroup.OnButtonCheckedListener listener) {
            if (!hasWatcher) {
                toggleGroup.addOnButtonCheckedListener(listener);
                hasWatcher = true;
            }
        }

        public void removeWatcher(MaterialButtonToggleGroup.OnButtonCheckedListener listener) {
            if (hasWatcher) {
                toggleGroup.removeOnButtonCheckedListener(listener);
                hasWatcher = false;
            }
        }


    }
}
