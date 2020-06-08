package com.saptarshi.mynotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saptarshi.mynotes.data.NotesDbHelper;
import com.saptarshi.mynotes.data.notesContract;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase mDatabase;
    FloatingActionButton plus ;
    noteAdapter mAdapter;
    noteAdapter.notesViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotesDbHelper dbHelper = new NotesDbHelper(this);
        mDatabase = dbHelper.getWritableDatabase();
        plus = findViewById(R.id.addItem);
        RecyclerView notesList = findViewById(R.id.notesList);
        notesList.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new  noteAdapter(this,getAllItems());
        notesList.setHasFixedSize(true);
        notesList.setAdapter(mAdapter);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,editActivity.class);
                startActivity(intent);
            }
        });

        mAdapter.setOnItemClickListener(new noteAdapter.OnItemClickListener() {
            @Override
            public void onItemCLick(int position, long id) {
                Intent intent = new Intent(MainActivity.this,editActivity.class);
                intent.putExtra("ID",id);
                startActivity(intent);
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.swapCursor(getAllItems());
    }

    private Cursor getAllItems() {
        return mDatabase.query(notesContract.notesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                notesContract.notesEntry.COLUMN_DATE + " ASC");

    }



}

