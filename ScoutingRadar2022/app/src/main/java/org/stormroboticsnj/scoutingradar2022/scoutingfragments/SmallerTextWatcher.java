package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

/**
 * A smaller version of TextWatcher that automatically implements the two methods that are
 * usually unused. It also replaces the third method with a more useful one, providing the
 * subclass with more information. This is a utility to reduce boilerplate code in the main methods.
 */
public abstract class SmallerTextWatcher implements TextWatcher {
    private final TextInputLayout mTextInputLayout;
    private final EditText mEditText;

    SmallerTextWatcher(TextInputLayout l) {
        mTextInputLayout = l;
        mEditText = l.getEditText();
    }

    @Override
    public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    @Override
    public final void onTextChanged(CharSequence s, int start, int before, int count) {
        // Do nothing here as it interrupts the user. Prefer afterTextChanged.
    }

    @Override
    public final void afterTextChanged(Editable s) {
        // Defer to a more useful method
        afterTextChanged(s.toString(), mTextInputLayout, mEditText);
    }

    public abstract void afterTextChanged(
            String input, TextInputLayout layout,
            EditText editText);
}
