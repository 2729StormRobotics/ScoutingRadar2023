package org.stormroboticsnj.scoutingradar2022;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class UiUtils {

    public static void imageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        final Animation anim_in = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {}

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        v.startAnimation(anim_out);
    }

    public static class ButtonInfo {
        public String name;
        public String abbreviation;
        public int id;
        public Button button;

        public ButtonInfo(String name, String abbreviation, int id, Button button) {
            this.name = name;
            this.abbreviation = abbreviation;
            this.id = id;
            this.button = button;
        }
    }

    public static class SpinnerInfo {
        public String name;
        public String[] contents;
        public int id;
        public Spinner spinner;

        public SpinnerInfo(
                String name, String[] contents, int id, Spinner spinner) {
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
