package org.stormrobotics.scoutingradar2022.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface MatchDao {
    @Insert
    void insert (WhooshMatch whooshMatch);
    @Insert
    void insertList (List<WhooshMatch> whooshMatches);
    @Insert
    void insertAll (WhooshMatch... whooshMatches);
    @Query("SELECT * FROM matches")
    public WhooshMatch[] getAllMatches();
}

