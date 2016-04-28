package com.example.amanarora.justnotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class editorActivity extends AppCompatActivity {

    public String action;
    public EditText editor;
    private String noteFilter;
    private String oldText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editor = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if(uri == null)
        {
            action = Intent.ACTION_INSERT;
            setTitle("New Note");
        }else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            editor.setText(oldText);

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(action.equals(Intent.ACTION_EDIT))
        {getMenuInflater().inflate(R.menu.menu_editor, menu);}
        return true;
    }
    private void finishEditing()
    {
        String newText = editor.getText().toString().trim();
        switch (action) {
            case Intent.ACTION_INSERT :
                if (newText.length() == 0)
                {
                    setResult(RESULT_CANCELED);
                }else {
                    insertNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if(newText.length() == 0)
                {
                    deleteNote();  // For deleting by backspace also
                }else if(oldText.equals(newText))
                {
                    setResult(RESULT_CANCELED);
                }else {
                        UpdateNote(newText);
                    Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show();
                }
        }

        finish();
    }

    private void UpdateNote(String newText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, newText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        setResult(RESULT_OK);

    }

    private void insertNote(String newText)
    {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, newText);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        
        switch ( item.getItemId())
        {
            case android.R.id.home :
                finishEditing();
                break;

            case R.id.action_delete :
                deleteNote();

        }
        return true;
    }

    private void deleteNote()
    {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
        Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }


    @Override
    public void onBackPressed()
    {
        finishEditing();
    }
}
