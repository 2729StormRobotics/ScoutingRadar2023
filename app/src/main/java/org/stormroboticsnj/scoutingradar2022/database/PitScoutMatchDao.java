package org.stormroboticsnj.scoutingradar2022.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

//adds data to database
@Dao
public interface PitScoutMatchDao {
    //insert one match
    @Insert
    void insert (PitScoutMatchData pitScoutMatchData);

    //insert a list of matches
    @Insert
    void insertList (List<PitScoutMatchData> pitScoutMatchData);

    //inserts multiple matches independently
    @Insert
    void insertAll (PitScoutMatchData... pitScoutMatchData);

    //gets all entries in table
    @Query("SELECT * FROM pit_scout_matches")
    PitScoutMatchData[] getAllMatches();

}
