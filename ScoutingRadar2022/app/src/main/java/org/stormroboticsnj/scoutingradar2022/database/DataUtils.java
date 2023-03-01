package org.stormroboticsnj.scoutingradar2022.database;

import android.util.Log;

import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitScoutData;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveMatchData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class DataUtils {

    public static byte[] compressData(List<?> dataList) {
        StringBuilder sb = new StringBuilder();
        for (Object data : dataList) {
            sb.append(data).append('!');
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outStream);
        try {
            deflaterOutputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            deflaterOutputStream.finish();
        } catch (IOException e) {
            Log.e(DataUtils.class.getSimpleName(), "Compress Data", e);
        }
        return outStream.toByteArray();
    }

    /**
     * @param compressedData a byte array of data to be added to the database
     * @return the data in a form that can be added to the database
     */
    public static String[] extractData(byte[] compressedData) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(outStream);
        try {
            inflaterOutputStream.write(compressedData);
            inflaterOutputStream.finish();
        } catch (IOException e) {
            Log.e(DataUtils.class.getSimpleName(), "Extract Data", e);
        }
        return (new String(outStream.toByteArray(), StandardCharsets.UTF_8)).split("!");
    }

    public static ObjectiveMatchData processObjectiveMatchData(List<Action> actions, int teamNumber, int matchNumber, String notes, boolean isRed) {
        List<Action> actionsCopy = new ArrayList<>(actions);

        // Sort actions chronologically
        //noinspection ComparatorCombinators
        Collections.sort(actionsCopy, (o1, o2) -> o1.getTimeSeconds() - o2.getTimeSeconds());
        HashMap<String, String> actionMap = new HashMap<>();
        // Make a map of action names to comma separated timestamps
        for (Action action : actionsCopy) {
            String subData = action.subAction.equals(Action.SUBACTION_NONE) ?
                             String.valueOf(action.getTimeSeconds()) : action.subAction;
            if (actionMap.containsKey(action.abbreviation)) {
                actionMap.put(action.abbreviation,
                        actionMap.get(action.abbreviation) + ";" + subData);
            } else {
                actionMap.put(action.abbreviation, subData);
            }

        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : actionMap.keySet()) {
            stringBuilder.append(key).append(":").append(actionMap.get(key)).append("|");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        stringBuilder.append("|Notes: ").append(notes);

        return new ObjectiveMatchData(teamNumber, matchNumber, isRed,
                stringBuilder.toString());
                new AllMatchData(teamNumber,matchNumber);

    }

    public static SubjectiveMatchData processSubjectiveData(List<Action> actions, int teamNumber, int matchNumber, boolean isRed) {
        List<Action> actionsCopy = new ArrayList<>(actions);

        // Make a map of action names to comma separated timestamps
        HashMap<String, String> actionMap = new HashMap<>();
        for (Action action : actionsCopy) {
            String subData = action.subAction.equals(Action.SUBACTION_NONE) ?
                             String.valueOf(action.getTimeSeconds()) : action.subAction;
            if (actionMap.containsKey(action.abbreviation)) {
                actionMap.put(action.abbreviation,
                        actionMap.get(action.abbreviation) + ";" + subData);
            } else {
                actionMap.put(action.abbreviation, subData);
            }

        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : actionMap.keySet()) {
            stringBuilder.append(key).append(":").append(actionMap.get(key)).append("|");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }


        return new SubjectiveMatchData(teamNumber, matchNumber, isRed,
                stringBuilder.toString());
        new AllMatchData(teamNumber,matchNumber);
    }

    public static PitScoutData processPitData(List<Action> actions, String notes, String motorInfo, int teamNumber) {
        List<Action> actionsCopy = new ArrayList<>(actions);

        // Make a map of action names to comma separated timestamps
        HashMap<String, String> actionMap = new HashMap<>();
        for (Action action : actionsCopy) {
            String subData = action.subAction.equals(Action.SUBACTION_NONE) ?
                             String.valueOf(action.getTimeSeconds()) : action.subAction;
            if (actionMap.containsKey(action.abbreviation)) {
                actionMap.put(action.abbreviation,
                        actionMap.get(action.abbreviation) + ";" + subData);
            } else {
                actionMap.put(action.abbreviation, subData);
            }

        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : actionMap.keySet()) {
            stringBuilder.append(key).append(":").append(actionMap.get(key)).append("|");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append("|Notes: ").append(notes);
        stringBuilder.append("|Motor Info: ").append(motorInfo);
        return new PitScoutData(teamNumber, stringBuilder.toString());
        new AllMatchData(teamNumber);
    }

    public static class Action {
        public static final String SUBACTION_NONE = "N/A";
        public final String abbreviation;
        public final String subAction;
        public final long time;


        public Action(String action, long time) {
            this.abbreviation = action;
            this.time = time;
            this.subAction = SUBACTION_NONE;
        }

        public Action(String action, String subAction) {
            this.abbreviation = action;
            this.subAction = subAction;
            this.time = Long.MAX_VALUE;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public int getTimeSeconds() {
            return (int) this.time / 1000;
        }

        public long getTime() {
            return time;
        }

        public String getSubAction() {
            return subAction;
        }

        public String getTimeString() {
            return String.format(Locale.getDefault(), "%02d:%02d", time / 60000,
                    (time % 60000) / 1000);
        }


    }

}
