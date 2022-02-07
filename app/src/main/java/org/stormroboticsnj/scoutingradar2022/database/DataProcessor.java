package org.stormroboticsnj.scoutingradar2022.database;

import android.content.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DataProcessor {

    String data;

    public static void processObjectiveMatchData(Context context, List<Action> actions, int teamNumber, int matchNumber, boolean isRed) {
        new Thread(() -> {
            // Sort actions chronologically
            //noinspection ComparatorCombinators
            Collections.sort(actions, (o1, o2) -> o1.getTimeSeconds() - o2.getTimeSeconds());
            HashMap<String, String> actionMap = new HashMap<>();
            // Make a map of action names to comma separated timestamps
            for (Action action : actions) {
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
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            String s = stringBuilder.toString();
            ObjectiveMatchData objectiveMatchData =
                    new ObjectiveMatchData(teamNumber, matchNumber, isRed,
                            stringBuilder.toString());
            AppDatabase.getInstance(context.getApplicationContext())
                       .objectiveMatchDao()
                       .insert(objectiveMatchData);

        }).start();
    }

    public static class Action {
        public final String abbreviation;
        public final String subAction;
        public final long time;

        private static final String SUBACTION_NONE = "N/A";


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
