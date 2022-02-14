package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import android.widget.Button;
import android.widget.Spinner;

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
        String abbreviation;
        String[] contents;
        String[] contents_abbreviations;
        int id;
        Spinner spinner;

        public SpinnerInfo(
                String name, String abbreviation, String[] contents,
                String[] contents_abbreviations, int id, Spinner spinner) {
            this.name = name;
            this.abbreviation = abbreviation;
            this.contents = contents;
            this.contents_abbreviations = contents_abbreviations;
            this.id = id;
            this.spinner = spinner;
        }

    }
}
