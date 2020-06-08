package com.saptarshi.mynotes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.saptarshi.mynotes.data.NotesDbHelper;
import com.saptarshi.mynotes.data.notesContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.saptarshi.mynotes.data.notesContract.HIGH_IMPORTANCE;
import static com.saptarshi.mynotes.data.notesContract.LOW_IMPORTANCE;
import static com.saptarshi.mynotes.data.notesContract.MEDIUM_IMPORTANCE;
import static com.saptarshi.mynotes.data.notesContract.notesEntry.COLUMN_DATE;
import static com.saptarshi.mynotes.data.notesContract.notesEntry.COLUMN_IMPORTANCE;
import static com.saptarshi.mynotes.data.notesContract.notesEntry.COLUMN_NOTE;

public class editActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    SQLiteDatabase mDatabase;
    final Calendar calendar = Calendar.getInstance();
    EditText dateText;
    TextInputLayout titleText;
    TextInputLayout descriptionText;
    Spinner impSpinner;
    int imp;
    ArrayAdapter<CharSequence> adapter;
    long id;
    boolean change = false;


    @SuppressLint("ClickableViewAccessibility")
    View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            change = true;
            return false;
        }

    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        NotesDbHelper dbHelper = new NotesDbHelper(this);
        mDatabase = dbHelper.getWritableDatabase();


        dateText = findViewById(R.id.layoutDate);
        titleText = findViewById(R.id.titleText);
        descriptionText = findViewById(R.id.description);


        impSpinner = findViewById(R.id.impSelector);
        adapter = ArrayAdapter.createFromResource(this, R.array.importance, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        impSpinner.setAdapter(adapter);


        Intent intent = getIntent();
        id = intent.getLongExtra("ID", -1);
        if (id == -1) {
            setTitle("Add a note");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit a note");
            fillDetails(id);
        }

        impSpinner.setOnTouchListener(listener);
        dateText.setOnTouchListener(listener);
        Objects.requireNonNull(titleText.getEditText()).setOnTouchListener(listener);
        Objects.requireNonNull(descriptionText.getEditText()).setOnTouchListener(listener);

        impSpinner.setOnItemSelectedListener(editActivity.this);
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(editActivity.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();

            }
        });
    }

    private void showUnsavedChangedDialog(DialogInterface.OnClickListener discardButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing");
        builder.setNegativeButton("DISCARD", discardButton)
                .setPositiveButton("Keep Editing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!change) {
            super.onBackPressed();
            return;
        } else {
            DialogInterface.OnClickListener discardButton = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            };
            showUnsavedChangedDialog(discardButton);
        }
    }

    private void fillDetails(long id) {
        String[] projection = {
                notesContract.notesEntry._ID,
                notesContract.notesEntry.COLUMN_NOTE,
                notesContract.notesEntry.COLUMN_DESCRIPTION,
                notesContract.notesEntry.COLUMN_DATE,
                notesContract.notesEntry.COLUMN_IMPORTANCE,

        };
        String selection = notesContract.notesEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor mCursor = mDatabase.query(notesContract.notesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (mCursor.moveToFirst()) {
            String note = mCursor.getString(mCursor.getColumnIndex(COLUMN_NOTE));
            String description = mCursor.getString(mCursor.getColumnIndex(notesContract.notesEntry.COLUMN_DESCRIPTION));
            String date = mCursor.getString(mCursor.getColumnIndex(COLUMN_DATE));
            int imp = mCursor.getInt(mCursor.getColumnIndex(COLUMN_IMPORTANCE));
            String sImp = null;
            if (imp == LOW_IMPORTANCE) sImp = "Low";
            if (imp == MEDIUM_IMPORTANCE) sImp = "Mid";
            if (imp == HIGH_IMPORTANCE) sImp = "High";

            Objects.requireNonNull(titleText.getEditText()).setText(note);
            dateText.setText(date);
            Objects.requireNonNull(descriptionText.getEditText()).setText(description);
            impSpinner.setSelection(adapter.getPosition(sImp));
        }
        mCursor.close();
    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };


    private void updateLabel() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.UK);
        dateText.setText(simpleDateFormat.format(calendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (id == -1) {
            MenuItem item = menu.findItem(R.id.delete);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();

        switch (item_id) {
            case R.id.delete:
                deleteNote();
                return true;
            case R.id.save:
                insertNotes();
                return true;
            default:
                return false;
        }
    }

    private void deleteNote() {
        String selection = notesContract.notesEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        long chk = mDatabase.delete(notesContract.notesEntry.TABLE_NAME, selection, selectionArgs);
        if (chk != 0) {
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        }
        finish();
    }


    private void insertNotes() {
        String titleString = Objects.requireNonNull(titleText.getEditText()).getText().toString();
        String descriptionString = Objects.requireNonNull(descriptionText.getEditText()).getText().toString();
        String dateString = dateText.getText().toString();
        int importance = imp;

        if (TextUtils.isEmpty(titleString) || TextUtils.isEmpty(descriptionString) || TextUtils.isEmpty(dateString)) {
            if (TextUtils.isEmpty(titleString)) {
                titleText.setError("Enter a title");
            }
            if (TextUtils.isEmpty(dateString)) {
                descriptionText.setError("Enter description");
            }
            if (TextUtils.isEmpty(dateString)) {
                Toast.makeText(this, "Give a valid date", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        ContentValues values = new ContentValues();

        values.put(notesContract.notesEntry.COLUMN_NOTE, titleString);
        values.put(notesContract.notesEntry.COLUMN_DESCRIPTION, descriptionString);
        values.put(notesContract.notesEntry.COLUMN_DATE, dateString);
        values.put(notesContract.notesEntry.COLUMN_IMPORTANCE, importance);


        if (id != -1) {
            String selection = notesContract.notesEntry._ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(id)};
            long chk = mDatabase.update(notesContract.notesEntry.TABLE_NAME, values, selection, selectionArgs);
            if (chk != 0) {
                Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            long chk = mDatabase.insert(notesContract.notesEntry.TABLE_NAME, null, values);
            if (chk != 0) {
                Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String string = (String) parent.getItemAtPosition(position);
        if (!TextUtils.isEmpty(string)) {
            if (string.equals("Low")) imp = notesContract.LOW_IMPORTANCE;
            if (string.equals("Mid")) imp = notesContract.MEDIUM_IMPORTANCE;
            if (string.equals("High")) imp = notesContract.HIGH_IMPORTANCE;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        imp = notesContract.LOW_IMPORTANCE;
    }
}
