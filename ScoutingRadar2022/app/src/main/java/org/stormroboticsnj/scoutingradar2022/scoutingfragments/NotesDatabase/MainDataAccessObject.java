package org.stormroboticsnj.scoutingradar2022.scoutingfragments.NotesDatabase;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import org.stormroboticsnj.scoutingradar2022.scoutingfragments.SubjectiveMatchNotes;

import java.util.List;

@Dao
public interface MainDataAccessObject {

    @Insert(onConflict = REPLACE)
    void insert (SubjectiveMatchNotes subjectiveMatchNotes);

    @Query("SELECT * FROM subjectivematchnotes")
    List<SubjectiveMatchNotes> getAll();

    @Query("UPDATE subjectiveMatchNotes SET title = :title, notes = :subjectiveMatchNotes WHERE ID = :id")
    void update(int id, String title, String subjectiveMatchNotes);

    @Delete
    void delete(SubjectiveMatchNotes subjectiveMatchNotes);

}
