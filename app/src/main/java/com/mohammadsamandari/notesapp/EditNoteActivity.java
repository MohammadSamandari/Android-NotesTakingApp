package com.mohammadsamandari.notesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        txtNote.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                SharedPreferences sharedPreferences=getSharedPreferences("com.mohammadsamandari.notesapp", Context.MODE_PRIVATE);
                MainActivity.notesArrayList.set(position,txtNote.getText().toString());
                MainActivity.notesArrayAdapter.notifyDataSetChanged();
                try {
                    sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(MainActivity.notesArrayList)).apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
    }
}
