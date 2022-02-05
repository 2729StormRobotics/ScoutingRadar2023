package org.stormrobotics.scoutingradar2022.database;

import java.util.ArrayList;
import java.util.List;

public class StringDecoder {
    private static String data;

    private static final String[] COLUMN_ABBREVIATIONS = new String[]{
            "AQ",
            "UH",
            "LH",
            "MS",
            "SC",
            "EC",
            "CP"
    };
    private static String[] organizedData= new String[COLUMN_ABBREVIATIONS.length];

    public StringDecoder(String data) {
        this.data = data;
    }

    public static String decodeString() {
        String organizedString = "";
        for (int i = 0; i<COLUMN_ABBREVIATIONS.length; i += data.indexOf("|", i+1)) {
            switch (data.substring(i+1,data.indexOf(":", i))){
                case "AQ":
                    break;
            }
        }
        return organizedString;
    }

}
