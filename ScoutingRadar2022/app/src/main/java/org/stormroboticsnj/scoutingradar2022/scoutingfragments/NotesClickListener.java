package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import androidx.cardview.widget.CardView;

public interface NotesClickListener {
    void onClick(SubjectiveMatchNotes subjectiveMatchNotes);
    void onLongClick(SubjectiveMatchNotes subjectiveMatchNotes, CardView cardView);

}
