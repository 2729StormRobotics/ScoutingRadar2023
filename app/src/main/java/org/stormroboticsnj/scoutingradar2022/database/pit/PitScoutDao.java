package org.stormroboticsnj.scoutingradar2022.database.pit;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

//adds data to database
@Dao
public interface PitScoutDao {
    //insert one match
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (PitScoutData pitScoutData);

    //insert a list of matches
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList (List<PitScoutData> pitScoutData);

    //inserts multiple matches independently
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll (PitScoutData... pitScoutData);

    //gets all entries in table
    @Query("SELECT * FROM pit_scout_matches")
    LiveData<List<PitScoutData>> getAllMatches();

    @Query("DELETE FROM pit_scout_matches")
    void deleteAll();
}
