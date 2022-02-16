package org.stormroboticsnj.scoutingradar2022.database;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.opencsv.CSVWriter;

import org.stormroboticsnj.scoutingradar2022.R;
import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveMatchData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVCreator {


    public static void processObjectiveCsv(ParcelFileDescriptor pfd) {
        new Thread(() -> {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());



                CSVCreator.createObjectiveCsv(fileOutputStream,
                        readPrefs(R.string.pref_key_obj_buttons, R.array.obj_buttons),
                        readPrefs(R.string.pref_key_obj_spinner, R.array.obj_spinners), data);

                // Let the document provider know you're done by closing the stream.
                fileOutputStream.close();
                pfd.close();
            } catch (IOException e) {
                Log.e("Export Data", "CSV Writing fail", e);
            }
        }).start();
    }

    public static void createObjectiveCsv(
            FileOutputStream output,
            String[] objectiveButtons, String[] objectiveSpinners,
            List<ObjectiveMatchData> dataList) throws IOException {

        // Setup CSVWriter
        OutputStreamWriter osr= new OutputStreamWriter(output);
        CSVWriter csvWriter = new CSVWriter(osr,
                CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

        // Create Column Names
        List<String> columnNames = new ArrayList<>();
        columnNames.add("team");
        columnNames.add("match");
        columnNames.add("is_red");
        for (String s : objectiveSpinners) {
            columnNames.add(s.substring(0, s.indexOf(":")).replace(" ", "_"));
        }
        Map<String, String> abbreviationsToNames = new HashMap<>();
        for (String s : objectiveButtons) {
            String[] split = s.split(":");
            columnNames.add(split[0]);
            abbreviationsToNames.put(split[1], split[0]);
        }
        Collections.sort(columnNames);

        csvWriter.writeNext(columnNames.toArray(new String[0]), true);

        Map<String, Integer> namesToIndices = new HashMap<>();
        for (int i = 0; i < columnNames.size(); i++) {
            namesToIndices.put(columnNames.get(i), i);
            Log.d("Col Name", columnNames.get(i));
        }

        // Add the data
        for (ObjectiveMatchData data : dataList) {
            String[] row = new String[columnNames.size()];

            row[0] = String.valueOf(data.getTeamNum());
            row[1] = String.valueOf(data.getMatchNum());
            row[2] = data.isRed() ? "T" : "F";

            String[] dataSplitIntoCols = data.getData().split("\\|");

            for (String col : dataSplitIntoCols) {
                String[] split = col.split(":");
                if (namesToIndices.containsKey(split[0])) {
                    Integer in = namesToIndices.get(split[0]);
                    if (in != null) {
                        row[in] = split[1];
                    }
                } else if (abbreviationsToNames.containsKey(split[0])) {
                    Integer in = namesToIndices.get(abbreviationsToNames.get(split[0]));
                    if (in != null) {
                        row[in] = split[1];
                    }
                }
            }

            csvWriter.writeNext(row, true);
        }

        csvWriter.close();

    }


}
