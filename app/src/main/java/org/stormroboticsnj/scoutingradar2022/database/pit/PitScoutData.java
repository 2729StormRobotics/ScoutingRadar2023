package org.stormroboticsnj.scoutingradar2022.database.pit;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

//defines the table and the identifier for each row
@Entity(tableName = "pit_scout_matches", primaryKeys = {"team_num", "match_num"})

public class PitScoutData {
    /*
    defines data for columns in table
    teamNum - team number
    matchNum - match number
    isRed - red/blue alliance
    autoData, teleopData, endgameData - data that was recorded during that phase
     */
    @ColumnInfo(name= "team_num")
    private int teamNum;
    @ColumnInfo(name= "match_num")
    private int matchNum;
    @ColumnInfo (name= "is_red")
    private boolean isRed;
    @ColumnInfo(name= "data")
    private String data;

    public PitScoutData(){}

    @Ignore
    public PitScoutData(int teamNum, int matchNum, boolean isRed, String data) {
        this.teamNum = teamNum;
        this.matchNum = matchNum;
        this.isRed = isRed;
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return
                teamNum +
                ";" + matchNum +
                ";" + (isRed ? 1 : 0) +
                ";" + data;
    }

    public static PitScoutData valueOf(String string){
        String[] values = string.split(";");
        if (values.length == 4) {
            return new PitScoutData(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]) == 1, values[3]);
        }
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
