package com.acfreeman.socialmediascanner;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class RegistrationInformation extends AppCompatActivity {

    SharedPreferences mPrefs;
    final String firstLaunchPref= "firstLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // second argument is the default to use if the preference can't be found
        Boolean firstLaunch = mPrefs.getBoolean(firstLaunchPref, true);

        if(firstLaunch) {

            setContentView(R.layout.activity_registration_information);

            Button nextBtn = (Button) findViewById(R.id.nextBtn);
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveData();
                    Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(startIntent);
                }
            });

            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(firstLaunchPref, false);
            editor.commit(); // Very important to save the preference
        }

        else {
            Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(startIntent);
        }


    }



    private void saveData(){

        EditText t1 = (EditText) findViewById(R.id.nameEditText);
        String name = (String) t1.getText().toString();
        EditText t2 = (EditText) findViewById(R.id.emailEditText);
        String email = (String) t2.getText().toString();
        EditText t3 = (EditText) findViewById(R.id.phoneEditText);
        String phone = (String) t3.getText().toString();

        Log.i("DATABASE","one");
        DBHelper mDbHelper = new DBHelper(getApplicationContext());
        Log.i("DATABASE","two");
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Log.i("DATABASE","three");

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBContract.DBOwner.NAME, name);
//        values.put(DBContract.DBOwner.EMAIL, email);
//        values.put(DBContract.DBOwner.PHONE, phone);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DBContract.DBOwner.TABLE_NAME, null, values);
    }


}
