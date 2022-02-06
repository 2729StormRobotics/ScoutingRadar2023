package org.stormrobotics.scoutingradar2022.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringDecoder {

    private static final String[] COLUMN_ABBREVIATIONS = new String[]{
            "AQ",
            "UH",
            "LH",
            "MS",
            "SC",
            "EC",
            "CP"
    };

    private static final String[] COLUMN_NAMES = new String[]{
            "Acquire",
            "Upper Hub",
            "Lower Hub",
            "Miss",
            "Start Climb",
            "End Climb",
            "Climb Position"
    };

    private static final Map<String, String> COLUMN_ABBREVIATION_TO_NAME = new java.util.HashMap<>();

    static {
        for (int i = 0; i < COLUMN_ABBREVIATIONS.length; i++) {
            COLUMN_ABBREVIATION_TO_NAME.put(COLUMN_ABBREVIATIONS[i], COLUMN_NAMES[i]);
        }
    }



    public static String decodeString(String data ) {

        String[] organizedData = new String[COLUMN_ABBREVIATIONS.length];

        String organizedString = "";
        for (int i = 0; i<COLUMN_ABBREVIATIONS.length; i += data.indexOf("|", i+1)) {
            switch (data.substring(i+1,data.indexOf(":", i))){
                case "AQ":
                    break;
            }
        }
        return organizedString;
    }

    public static String[][] stringToTable(String data) {
        // Columns are separated by |
        // Each column is in the format header:value
        // The header is the column name, and the value is the data
        // Convert data string to table String[][]

        // Split data into columns
        String[] columns = data.split("\\|");
        // Split each column into header:value pairs
        String[][] table = new String[2][columns.length];
        for (int i = 0; i < columns.length; i++) {
            table[0][i] = getNameFromAbbreviation(columns[i].substring(0, columns[i].indexOf(":")));
            table[1][i] = columns[i].substring(columns[i].indexOf(":") + 1);
        }
        return table;
    }

    public static String getNameFromAbbreviation(String abbreviation) {
        return COLUMN_ABBREVIATION_TO_NAME.get(abbreviation);
    }

}
