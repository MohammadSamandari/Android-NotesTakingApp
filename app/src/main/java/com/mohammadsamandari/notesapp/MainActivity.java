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
    static ArrayList<String> notesArrayList,notesArrayListLimited;
    static ArrayAdapter notesArrayAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Initializing the Variables:
        notesListView = findViewById(R.id.noteListView);
        notesArrayList = new ArrayList<>();
        notesArrayListLimited=new ArrayList<>();    //Using this arraylist for showing only parts of main notesArrayList items.
        sharedPreferences = getSharedPreferences("com.mohammadsamandari.notesapp", Context.MODE_PRIVATE);

        //  Checking Apps first run or not
        if (!sharedPreferences.contains("notes")) {
            Log.i("Lord-SharedPreferences", "First Time Run");
            //  Adding the new note to the notesArrayList.
            notesArrayList.add("Sample Note");
        } else {
            Log.i("Lord-SharedPreferences", "Not First Time Run");
            //  Importing previous data into notesArrayList.
            try {
                notesArrayList.clear();
                notesArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("notes", ObjectSerializer.serialize(new ArrayList<String>())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //  Updating the array adapter with data.
        notesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, notesArrayListLimited);
        notesListView.setAdapter(notesArrayAdapter);
        populateNotesArrayListLimited();

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

    //  Getting the note that we want to edit and open the edit note activity with that notes.
    private void openEditNoteActivity(String note,int position) {
        Intent intent = new Intent(getApplicationContext(), EditNoteActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("position",position);
        startActivity(intent);
    }

    //  Showing Delete note dialog, and delete and save note.
    private void showDeleteAlertDialog(final int position) {
        new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Are you sure?")
                .setMessage("Do you want to delete this note?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //  Removing the following item from notesArrayList.
                        notesArrayList.remove(position);
                        populateNotesArrayListLimited();

                        //  Saving the changes.
                        try {
                            sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(notesArrayList)).apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //  Notify the User
                        Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    //  Adding items to notesArrayListLimited to be shown in the list view.
    public static void populateNotesArrayListLimited() {
        //  This method is created so that if a note is more than 30 characters, it limits the number of characters that is shown in the list view.
        notesArrayListLimited.clear();
        for(int i=0;i<notesArrayList.size();i++){
            if(notesArrayList.get(i).length()>30){
                String limitedNote=notesArrayList.get(i).substring(0,30)+" . . .";
                notesArrayListLimited.add(limitedNote);
            }else{
                notesArrayListLimited.add(notesArrayList.get(i));
            }
        }
        notesArrayAdapter.notifyDataSetChanged();
    }

}
