package org.stormroboticsnj.scoutingradar2022.database.subjective;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import org.stormroboticsnj.scoutingradar2022.R;

public class ChangeBackgroundByButtonClick extends AppCompatActivity {
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_subjective_match);
        view = this.getWindow().getDecorView();
    }

    public void goRed(View v){
        view.setBackgroundResource(R.color.backgroundRed);
    }
}