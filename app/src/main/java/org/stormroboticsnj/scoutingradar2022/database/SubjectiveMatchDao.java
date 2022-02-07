package org.stormroboticsnj.scoutingradar2022.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

//adds data to database
@Dao
public interface SubjectiveMatchDao {
    //insert one match
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (SubjectiveMatchData subjectiveMatchData);

    //insert a list of matches
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList (List<SubjectiveMatchData> subjectiveMatchData);

    //inserts multiple matches independently
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll (SubjectiveMatchData... subjectiveMatchData);

    //gets all entries in table
    @Query("SELECT * FROM subjective_matches")
    SubjectiveMatchData[] getAllMatches();
}

