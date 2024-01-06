package com.nafsoft.todolistapplication.listeners;

import com.nafsoft.todolistapplication.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
