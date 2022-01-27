package org.stormrobotics.scoutingradar2022.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

//adds data to database
@Dao
public interface SubjectiveMatchDao {
    //insert one match
    @Insert
    void insert (SubjectiveMatchData subjectiveMatchData);

    //insert a list of matches
    @Insert
    void insertList (List<SubjectiveMatchData> subjectiveMatchData);

    //inserts multiple matches independently
    @Insert
    void insertAll (SubjectiveMatchData... subjectiveMatchData);

    //gets all entries in table
    @Query("SELECT * FROM subjective_matches")
    public SubjectiveMatchData[] getAllMatches();
}

