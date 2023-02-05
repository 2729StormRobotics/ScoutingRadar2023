package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import androidx.cardview.widget.CardView;

public interface NotesClickListener {

    // There are blocks of different notes that you can make, if you click on it
    // you can access it easily
    void onClick(SubjectiveMatchNotes subjectiveMatchNotes);

    // On long click, you will receive the option
    // to delete
    void onLongClick(SubjectiveMatchNotes subjectiveMatchNotes, CardView cardView);

}
