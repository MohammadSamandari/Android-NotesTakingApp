package com.mohammadsamandari.notesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

public class EditNoteActivity extends AppCompatActivity {

    //  Defining Variables:
    EditText txtNote;
    String note;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        //  Initializing the Variables:
        txtNote = findViewById(R.id.txtNote);

        //  Getting Intent and extras inside it.
        Intent intent = getIntent();
        note = intent.getStringExtra("note");
        position=intent.getIntExtra("position",0);

        //  Populating the edit text with the notes.
        txtNote.setText(note);

        txtNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainActivity.notesArrayList.set(position, String.valueOf(s));
                MainActivity.populateNotesArrayListLimited();
                try {
                    SharedPreferences sharedPreferences=getSharedPreferences("com.mohammadsamandari.notesapp", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(MainActivity.notesArrayList)).apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}
