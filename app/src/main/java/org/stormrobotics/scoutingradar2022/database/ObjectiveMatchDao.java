package org.stormrobotics.scoutingradar2022.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

//adds data to database
@Dao
public interface ObjectiveMatchDao {
    //insert one match
    @Insert
    Completable insert (ObjectiveMatchData objectiveMatchData);


    //insert a list of matches
    @Insert
    Completable insertList (List<ObjectiveMatchData> objectiveMatchData);

    //inserts multiple matches independently
    @Insert
    Completable insertAll (ObjectiveMatchData... objectiveMatchData);

    //gets all entries in table
    @Query("SELECT * FROM objective_matches")
    LiveData<List<ObjectiveMatchData>> getAllMatches();

    // Deletes entire table
    /* BE CAREFUL */
    @Query("DELETE FROM objective_matches")
    Completable deleteAll();
}

