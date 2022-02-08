package org.stormroboticsnj.scoutingradar2022.database;

import android.content.Context;

import org.stormroboticsnj.scoutingradar2022.database.ObjectiveMatchData;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.Deflater;

public class DataUtils {

    public static String compressObjectiveMatchData(List<ObjectiveMatchData> dataList){
        StringBuilder sb = new StringBuilder();
        for (ObjectiveMatchData data : dataList){
            sb.append(data).append('!');
        }
//        try {
//            Deflater compressor = new Deflater();
//            compressor.setInput(sb.toString().getBytes(StandardCharsets.UTF_8));
//            compressor.finish();
//            // compressor.deflate
//        }
        return "";
    }

    public static ObjectiveMatchData processObjectiveMatchData(List<Action> actions, int teamNumber, int matchNumber, boolean isRed) {
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
                        actionMap.get(action.abbreviation) + "," + subData);
            } else {
                actionMap.put(action.abbreviation, subData);
            }

        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : actionMap.keySet()) {
            stringBuilder.append(key).append(":").append(actionMap.get(key)).append("|");
        }
        if (stringBuilder.length() > 0) stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        String s = stringBuilder.toString();

        return new ObjectiveMatchData(teamNumber, matchNumber, isRed,
                stringBuilder.toString());
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
