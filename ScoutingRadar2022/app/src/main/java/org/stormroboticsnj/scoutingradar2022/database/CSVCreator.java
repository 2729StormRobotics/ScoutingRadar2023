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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVCreator {


    public static void createObjectiveCsv(
            ParcelFileDescriptor pfd, String[] buttons, String[] abbreviations,
            String[] spinners, List<ObjectiveMatchData> data) {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            objCsv(fileOutputStream, buttons, abbreviations, spinners, data);

            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (IOException e) {
            Log.e("Export Data", "CSV Writing fail", e);
        }

    }

    public static void createSubjectiveCsv(
            ParcelFileDescriptor pfd,
            String[] spinners, List<SubjectiveMatchData> data) {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            subCsv(fileOutputStream, spinners, data);

            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (IOException e) {
            Log.e("Export Data", "CSV Writing fail", e);
        }

    }

    public static void createPitCsv(
            ParcelFileDescriptor pfd,
            String[] spinners, List<PitScoutData> data) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            pitCsv(fileOutputStream, spinners, data);

            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (IOException e) {
            Log.e("Export Data", "CSV Writing fail", e);
        }
    }

    private static CSVWriter getCsvWriter(FileOutputStream output) {
        // Setup CSVWriter
        OutputStreamWriter osr = new OutputStreamWriter(output);
        return new CSVWriter(osr,
                CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
    }

    private static void objCsv(
            FileOutputStream output,
            String[] objectiveButtons, String[] objectiveAbbreviations, String[] objectiveSpinners,
            List<ObjectiveMatchData> dataList) throws IOException {

        // Setup CSV Writer
        CSVWriter csvWriter = getCsvWriter(output);

        // Create Column Names
        List<String> columnNames = new ArrayList<>();

        columnNames.add("team");
        columnNames.add("match");
        columnNames.add("is_red");

        // Add all the buttons
        columnNames.addAll(Arrays.asList(objectiveAbbreviations));

        // Add all the spinner names, but sorted alphabetically first
        // We need to do this because when we are not guaranteed an order for the spinners, at least
        // we can standardize output to be alphabetical.
        List<String> spinnerNames = new ArrayList<>();
        for (String s : objectiveSpinners) {
            spinnerNames.add(s.substring(0, s.indexOf(":")).replace(" ", "_"));
        }
        Collections.sort(spinnerNames);
        columnNames.addAll(spinnerNames);

        // Write the column names (header) to the CSV file
        csvWriter.writeNext(columnNames.toArray(new String[0]), true);

        // Reverse-map column names to indices for fast lookup later
        Map<String, Integer> namesToIndices = new HashMap<>();
        for (int i = 0; i < columnNames.size(); i++) {
            namesToIndices.put(columnNames.get(i), i);
        }

        // Map abbreviations to names as
        Map<String, String> abbreviationsToNames = new HashMap<>();
        for (int i = 0; i < objectiveButtons.length; i++) {
            abbreviationsToNames.put(objectiveAbbreviations[i], objectiveButtons[i]);
        }

        // Add the data
        for (ObjectiveMatchData data : dataList) {
            // Build this row of data
            String[] row = new String[columnNames.size()];

            // Three known columns
            row[0] = String.valueOf(data.getTeamNum());
            row[1] = String.valueOf(data.getMatchNum());
            row[2] = data.isRed() ? "T" : "F";

            // The remaining user-defined columns
            String[] dataSplitIntoCols = data.getData().split("\\|");

            for (String col : dataSplitIntoCols) {
                String[] split = col.split(":");
                split[0] = split[0].replace(' ', '_');
                // Lookup the index
                if (namesToIndices.containsKey(split[0])) {
                    Integer in = namesToIndices.get(split[0]);
                    if (in != null) {
                        row[in] = split[1];
                    }
                    // If the index was not found then we have an abbreviation
                } else if (abbreviationsToNames.containsKey(split[0])) {
                    Integer in = namesToIndices.get(abbreviationsToNames.get(split[0]));
                    if (in != null) {
                        row[in] = split[1];
                    }
                }
            }

            // Write the row
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

        // Add the spinners
        for (String s : subSpinners) {
            columnNames.add(s.substring(0, s.indexOf(":")).replace(" ", "_"));
        }

        // Sort column names, but keep team, match, and alliance first
        // We need to do this because when we are not guaranteed an order for the spinners, at least
        // we can standardize output to be alphabetical.
        List<String> columnNamesSorted = new ArrayList<>();

        columnNamesSorted.add("team");
        columnNamesSorted.add("match");
        columnNamesSorted.add("is_red");

        Collections.sort(columnNames);
        columnNamesSorted.addAll(columnNames);

        columnNames = columnNamesSorted;

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
                split[0] = split[0].replace(' ', '_');
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

        // Add spinners
        for (String s : pitSpinners) {
            columnNames.add(s.substring(0, s.indexOf(":")).replace(" ", "_"));
        }

        // Sort column names, but keep team first
        List<String> columnNamesSorted = new ArrayList<>();

        columnNamesSorted.add("team");

        Collections.sort(columnNames);
        columnNamesSorted.addAll(columnNames);

        columnNamesSorted.add("Notes");
        columnNamesSorted.add("Motor Info");

        columnNames = columnNamesSorted;

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
                split[0] = split[0].replace(' ', '_');
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
