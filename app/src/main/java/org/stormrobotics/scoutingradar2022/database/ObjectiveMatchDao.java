package org.stormrobotics.scoutingradar2022.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

//adds data to database
@Dao
public interface ObjectiveMatchDao {
    //insert one match
    @Insert
    void insert (ObjectiveMatchData objectiveMatchData);

    //insert a list of matches
    @Insert
    void insertList (List<ObjectiveMatchData> objectiveMatchData);

    //inserts multiple matches independently
    @Insert
    void insertAll (ObjectiveMatchData... objectiveMatchData);

    //gets all entries in table
    @Query("SELECT * FROM objective_matches")
    public LiveData<List<ObjectiveMatchData>> getAllMatches();
}

