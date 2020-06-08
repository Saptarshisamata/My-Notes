package com.saptarshi.mynotes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.saptarshi.mynotes.data.notesContract.notesEntry.*;


public class NotesDbHelper extends SQLiteOpenHelper {

    public final static  String DATABASE_NAME = "notes.db";
    public final static  int DATABASE_VERSION = 1;

    public NotesDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_NOTES_TABLE =" CREATE TABLE "+TABLE_NAME + "("
                + _ID +" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + COLUMN_NOTE + " TEXT NOT NULL ,"
                + COLUMN_DESCRIPTION + " TEXT NOT NULL ,"
                + COLUMN_IMPORTANCE + " INTEGER NOT NULL DEFAULT 0 ,"
                + COLUMN_DATE + " TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ) ;";

        db.execSQL(SQL_CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //database is still in version 1 .. so nothing to do here..
    }
}
