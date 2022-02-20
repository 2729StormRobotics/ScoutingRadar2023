package org.stormroboticsnj.scoutingradar2022.database.objective;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

//adds data to database
@Dao
public interface ObjectiveMatchDao {
    //insert one match
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ObjectiveMatchData objectiveMatchData);

    //insert a list of matches
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<ObjectiveMatchData> objectiveMatchData);

    //inserts multiple matches independently
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ObjectiveMatchData> objectiveMatchData);

    //gets all entries in table
    @Query("SELECT * FROM objective_matches")
    LiveData<List<ObjectiveMatchData>> getLiveMatches();

    @Query("SELECT * FROM objective_matches")
    Maybe<List<ObjectiveMatchData>> getAllMatches();

    // Deletes entire table
    /* BE CAREFUL */
    @Query("DELETE FROM objective_matches")
    void deleteAll();
}

