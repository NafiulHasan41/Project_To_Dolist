package com.nafsoft.todolistapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nafsoft.todolistapplication.dao.NoteDao;
import com.nafsoft.todolistapplication.entities.Note;

@Database(entities = Note.class, version = 1,exportSchema = false)
public abstract class NoteDataBase extends RoomDatabase {

    private static NoteDataBase noteDataBase;

    public static synchronized NoteDataBase getDatabase(Context context){
        if(noteDataBase == null)
        {
            noteDataBase = Room.databaseBuilder(context,
                    NoteDataBase.class,
                    "note_db").build();
        }

        return noteDataBase;
    }

    public abstract NoteDao noteDao();


}
