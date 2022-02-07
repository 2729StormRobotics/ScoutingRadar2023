package org.stormroboticsnj.scoutingradar2022.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

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

    public ObjectiveMatchData(int teamNum, int matchNum, boolean isRed, String data) {
        this.teamNum = teamNum;
        this.matchNum = matchNum;
        this.isRed = isRed;
        this.data = data;
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
