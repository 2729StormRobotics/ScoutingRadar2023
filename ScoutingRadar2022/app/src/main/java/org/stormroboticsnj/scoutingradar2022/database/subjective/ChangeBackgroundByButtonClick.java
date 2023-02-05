package org.stormroboticsnj.scoutingradar2022.database.subjective;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import org.stormroboticsnj.scoutingradar2022.R;

public class ChangeBackgroundByButtonClick extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button subjective_button_red, subjective_button_blue;
        ScrollView layout;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_subjective_match);

        subjective_button_red = findViewById(R.id.subjective_button_red);
        subjective_button_blue = findViewById(R.id.subjective_button_blue);
        layout = findViewById(R.id.scrollview);

        subjective_button_red.setOnClickListener(view -> layout.setBackgroundColor(Color.RED));

        subjective_button_blue.setOnClickListener(view -> layout.setBackgroundColor(Color.BLUE));

    }





}