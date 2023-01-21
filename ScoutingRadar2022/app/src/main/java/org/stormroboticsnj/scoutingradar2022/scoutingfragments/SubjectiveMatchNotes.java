package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "subjectivematchnotes")
public class SubjectiveMatchNotes implements Serializable {

    @PrimaryKey(autoGenerate = true)
    int ID = 0;

    // Creates Three String variables to hold notes, date, and title
    @ColumnInfo(name="notes")
    String notes = "";

    @ColumnInfo(name = "date")
    String date = "";

    @ColumnInfo(name = "title")
    String title = "";

    // Gets and sets the current ID for notes written
    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }

    // Gets the string content of the notes and sets it
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Gets the date and sets it
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    // Gets the title and sets it
    public String getTitle() {
        return title;
    }
    public void setTitle(String date) {
        this.title = title;
    }


}
