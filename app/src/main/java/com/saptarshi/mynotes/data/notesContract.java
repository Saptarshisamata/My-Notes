package com.saptarshi.mynotes.data;

import android.provider.BaseColumns;

public class notesContract {

    private notesContract(){}

    public static final class notesEntry{
        public static final String TABLE_NAME = "notes";
        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMPORTANCE = "importance";
        public static final String COLUMN_DATE = "date";

    }

    public static final int LOW_IMPORTANCE = 0;
    public static final int MEDIUM_IMPORTANCE = 1;
    public static final int HIGH_IMPORTANCE = 2;

}
