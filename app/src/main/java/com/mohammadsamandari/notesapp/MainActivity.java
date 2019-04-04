package com.mohammadsamandari.notesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Defining the Variables.
    ListView notesListView;
    static ArrayList<String> notesArrayList;
    static ArrayAdapter notesArrayAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Initializing the Variables:
        notesListView = findViewById(R.id.noteListView);
        notesArrayList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("com.mohammadsamandari.notesapp", Context.MODE_PRIVATE);

        //  Checking Apps first run or not
        if (!sharedPreferences.contains("notes")) {
            Log.i("Lord-SharedPreferences", "First Time Run");
            //  Adding the new note to the notesArrayList.
            notesArrayList.add("Sample Note");
            saveNewNote();
        } else {
            Log.i("Lord-SharedPreferences", "Not First Time Run");
            //  Importing previous data into notesArrayList.
            try {
                notesArrayList.clear();
                notesArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("notes", ObjectSerializer.serialize(new ArrayList<String>())));
                Log.i("Lord-Test", String.valueOf(notesArrayList.size()));
                Log.i("Lord-SharedPreferences", "Not First Time - Try Part Happened");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //  Updating the array adapter with new data.
        notesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, notesArrayList);
        notesListView.setAdapter(notesArrayAdapter);

        //  Listview on item click Listener:
        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  Getting to note from array list, moving to the edit note activity.
                String note = notesArrayList.get(position);
                openEditNoteActivity(note,position);
            }
        });

        //ListView on ite Long click listener
        notesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //  Showing Alert Dialog for deleting the note.
                showDeleteAlertDialog(position);
                return true;
            }
        });

    }

    //  Adding the Menu to the main Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //  Adding on item select for the menu.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        //  Check to see if Add New Note button is clicked.
        if (item.getItemId() == R.id.btnAddNote) {
            //  Calling openEditNoteActivity Method.
            notesArrayList.add("");
            int positionToPass=notesArrayList.size()-1;
            openEditNoteActivity("",positionToPass);
            return true;
        }
        return false;
    }

    private void saveNewNote() {
        //saving the whole notesArrayList int shared preferences.
        try {
            sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(notesArrayList)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openEditNoteActivity(String note,int position) {
        //  Getting the note that we want to edit and open the edit note activity with that notes.
        Intent intent = new Intent(getApplicationContext(), EditNoteActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("position",position);
        startActivity(intent);
    }

    private void showDeleteAlertDialog(final int position) {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Are you sure?")
                .setMessage("Do you want to delete this note?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //  Removing the following item from notesArrayList.
                        notesArrayList.remove(position);

                        //  Saving the changes.
                        saveNewNote();

                        //  Updating the UI
                        notesArrayAdapter.notifyDataSetChanged();
                        //  Notify the User
                        Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }


}
