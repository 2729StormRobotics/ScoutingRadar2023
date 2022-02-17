package org.stormroboticsnj.scoutingradar2022.database;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.opencsv.CSVWriter;

import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitScoutData;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveMatchData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVCreator {


    public static void createObjectiveCsv(
            ParcelFileDescriptor pfd, String[] buttons,
            String[] spinners, List<ObjectiveMatchData> data) {
        new Thread(() -> {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                objCsv(fileOutputStream, buttons, spinners, data);

                // Let the document provider know you're done by closing the stream.
                fileOutputStream.close();
                pfd.close();
            } catch (IOException e) {
                Log.e("Export Data", "CSV Writing fail", e);
            }
        }).start();
    }

    public static void createSubjectiveCsv(
            ParcelFileDescriptor pfd,
            String[] spinners, List<SubjectiveMatchData> data) {
        new Thread(() -> {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                subCsv(fileOutputStream, spinners, data);

                // Let the document provider know you're done by closing the stream.
                fileOutputStream.close();
                pfd.close();
            } catch (IOException e) {
                Log.e("Export Data", "CSV Writing fail", e);
            }
        }).start();
    }

    public static void createPitCsv(
            ParcelFileDescriptor pfd,
            String[] spinners, List<PitScoutData> data) {
        new Thread(() -> {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                pitCsv(fileOutputStream, spinners, data);

                // Let the document provider know you're done by closing the stream.
                fileOutputStream.close();
                pfd.close();
            } catch (IOException e) {
                Log.e("Export Data", "CSV Writing fail", e);
            }
        }).start();
    }

    private static CSVWriter getCsvWriter(FileOutputStream output) {
        // Setup CSVWriter
        OutputStreamWriter osr = new OutputStreamWriter(output);
        CSVWriter csvWriter = new CSVWriter(osr,
                CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

        return csvWriter;
    }

    private static void objCsv(
            FileOutputStream output,
            String[] objectiveButtons, String[] objectiveSpinners,
            List<ObjectiveMatchData> dataList) throws IOException {

       CSVWriter csvWriter = getCsvWriter(output);

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

    private static void subCsv(
            FileOutputStream output,
            String[] subSpinners,
            List<SubjectiveMatchData> dataList) throws IOException {

        // Setup CSVWriter
        CSVWriter csvWriter = getCsvWriter(output);


        // Create Column Names
        List<String> columnNames = new ArrayList<>();
        columnNames.add("team");
        columnNames.add("match");
        columnNames.add("is_red");
        for (String s : subSpinners) {
            columnNames.add(s.substring(0, s.indexOf(":")).replace(" ", "_"));
        }

        Collections.sort(columnNames);

        csvWriter.writeNext(columnNames.toArray(new String[0]), true);

        Map<String, Integer> namesToIndices = new HashMap<>();
        for (int i = 0; i < columnNames.size(); i++) {
            namesToIndices.put(columnNames.get(i), i);
        }

        // Add the data
        for (SubjectiveMatchData data : dataList) {
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
                }
            }

            csvWriter.writeNext(row, true);
        }

        csvWriter.close();
    }

    private static void pitCsv(
            FileOutputStream output,
            String[] pitSpinners,
            List<PitScoutData> dataList) throws IOException {

        // Setup CSVWriter
        CSVWriter csvWriter = getCsvWriter(output);


        // Create Column Names
        List<String> columnNames = new ArrayList<>();
        columnNames.add("team");
        for (String s : pitSpinners) {
            columnNames.add(s.substring(0, s.indexOf(":")).replace(" ", "_"));
        }

        Collections.sort(columnNames);

        csvWriter.writeNext(columnNames.toArray(new String[0]), true);

        Map<String, Integer> namesToIndices = new HashMap<>();
        for (int i = 0; i < columnNames.size(); i++) {
            namesToIndices.put(columnNames.get(i), i);
        }

        // Add the data
        for (PitScoutData data : dataList) {
            String[] row = new String[columnNames.size()];

            row[0] = String.valueOf(data.getTeamNum());

            String[] dataSplitIntoCols = data.getData().split("\\|");

            for (String col : dataSplitIntoCols) {
                String[] split = col.split(":");
                if (namesToIndices.containsKey(split[0])) {
                    Integer in = namesToIndices.get(split[0]);
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
