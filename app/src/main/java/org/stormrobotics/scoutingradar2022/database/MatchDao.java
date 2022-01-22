package org.stormrobotics.scoutingradar2022.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

//adds data to database
@Dao
public interface MatchDao {
    //insert one match
    @Insert
    void insert (WhooshMatch whooshMatch);

    //insert a list of matches
    @Insert
    void insertList (List<WhooshMatch> whooshMatches);

    //inserts multiple matches independently
    @Insert
    void insertAll (WhooshMatch... whooshMatches);

    //gets all entries in table
    @Query("SELECT * FROM matches")
    public WhooshMatch[] getAllMatches();
}

