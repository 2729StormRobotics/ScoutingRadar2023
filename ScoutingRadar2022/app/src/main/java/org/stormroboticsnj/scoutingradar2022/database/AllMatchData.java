package org.stormroboticsnj.scoutingradar2022.database;

import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveMatchData;

public class AllMatchData extends ObjectiveMatchData {

    public AllMatchData() {}

    public AllMatchData(int teamNum) {
        super(teamNum);

    }

    public AllMatchData(int teamNum, int matchNum) {
        super(teamNum, matchNum);
    }


}
