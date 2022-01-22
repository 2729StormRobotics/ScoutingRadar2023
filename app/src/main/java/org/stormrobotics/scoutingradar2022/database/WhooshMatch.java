package org.stormrobotics.scoutingradar2022.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "matches", primaryKeys = {"team_num", "match_num"})

public class WhooshMatch {
    @ColumnInfo(name= "team_num")
    private int teamNum;
    @ColumnInfo(name= "match_num")
    private int matchNum;
    @ColumnInfo (name= "is_red")
    private boolean isRed;
    @ColumnInfo(name= "auto_data")
    private String autoData;
    @ColumnInfo(name= "teleop_data")
    private String teleopData;
    @ColumnInfo(name= "endgame_data")
    private String endgameData;

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

    public String getAutoData() {
        return autoData;
    }

    public void setAutoData(String autoData) {
        this.autoData = autoData;
    }

    public String getTeleopData() {
        return teleopData;
    }

    public void setTeleopData(String teleopData) {
        this.teleopData = teleopData;
    }

    public String getEndgameData() {
        return endgameData;
    }

    public void setEndgameData(String endgameData) {
        this.endgameData = endgameData;
    }

}
