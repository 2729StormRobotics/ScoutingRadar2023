package org.stormroboticsnj.scoutingradar2022.database.objective;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import org.apache.commons.lang3.ArrayUtils;

//defines the table and the identifier for each row
@Entity(tableName = "objective_matches", primaryKeys = {"team_num", "match_num"})

public class ObjectiveMatchData {
    /*
    defines data for columns in table
    teamNum - team number
    matchNum - match number
    isRed - red/blue alliance
    data - data that was recorded during that phase
     */
    @ColumnInfo(name= "team_num")
    private int teamNum;
    @ColumnInfo(name= "match_num")
    private int matchNum;
    @ColumnInfo (name= "is_red")
    private boolean isRed;
    @ColumnInfo(name= "data")
    private String data;

    // Constructors

    public ObjectiveMatchData() {}

    @Ignore
    public ObjectiveMatchData(int teamNum, int matchNum) {
        this.teamNum = teamNum;
        this.matchNum = matchNum;
    }

    @Ignore
    public ObjectiveMatchData(int teamNum, int matchNum, boolean isRed, String data) {
        this.teamNum = teamNum;
        this.matchNum = matchNum;
        this.isRed = isRed;
        this.data = data;
    }

    @Override
    public String toString() {
        return
                teamNum +
               ";" + matchNum +
               ";" + (isRed ? 1 : 0) +
               ";" + data;
    }

    @Nullable
    public static ObjectiveMatchData valueOf(String string) {

        String[] values = string.split(";", 4);

        String index3 = values[3];
        String[] newValues = index3.split("\\|");

        int valueLength = values.length;
        int newValueLength = newValues.length;
//        values = ArrayUtils.removeElement(values, index3);


        String[] combinedArrays = new String[valueLength + newValueLength];
        System.arraycopy(values, 0, combinedArrays,0,valueLength);
        System.arraycopy(newValues, 0, combinedArrays,valueLength,newValueLength);

//        newValues.split("|");
//        values = string.split("|");
//        if (values.length == 4) {
            try {
                return new ObjectiveMatchData(Integer.parseInt(values[0]),
                        Integer.parseInt(values[1]), Integer.parseInt(values[2]) == 1, values[3]);
            } catch (NumberFormatException e){
                Log.e("Objective Match Data", "Non number team number", e);
            }
//        }
        return null;
    }

    //getters and setters for member variables

    public int getTeamNum() {
        return teamNum;
    }

    public void setTeamNum(int teamNum) {
        this.teamNum = teamNum;
    }

    public int getMatchNum() {
        return matchNum;
    }

    public void setMatchNum(int matchNum) {
        this.matchNum = matchNum;
    }

    public boolean isRed() {
        return isRed;
    }

    public void setRed(boolean red) {
        isRed = red;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
