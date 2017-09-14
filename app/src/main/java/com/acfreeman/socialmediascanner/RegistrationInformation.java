package com.acfreeman.socialmediascanner;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;

import static java.security.AccessController.getContext;

public class RegistrationInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        values.put(DBContract.DBEntry.NAME, name);
        values.put(DBContract.DBEntry.EMAIL, email);
        values.put(DBContract.DBEntry.PHONE, phone);

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DBContract.DBEntry.TABLE_NAME, null, values);
    }


//    private void saveData(){
//
//        EditText t1 = (EditText) findViewById(R.id.nameEditText);
//        String name = (String) t1.getText().toString();
//        EditText t2 = (EditText) findViewById(R.id.emailEditText);
//        String email = (String) t2.getText().toString();
//        EditText t3 = (EditText) findViewById(R.id.phoneEditText);
//        String phone = (String) t3.getText().toString();
//
//        JSONObject people = new JSONObject();
//        try {
//            people.put("id", "0");
//            people.put("name", name);
//            people.put("email", email);
//            people.put("phone", phone);
//
//
//
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        JSONArray jsonArray = new JSONArray();
//
//        jsonArray.put(people);
//
//        JSONObject contactsObj = new JSONObject();
//        try {
//            contactsObj.put(, jsonArray);
//            String jsonStr = contactsObj.toString();
//            writeToFile(jsonStr);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void writeToFile(String data) {
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("ppl.txt", Context.MODE_PRIVATE));
//            outputStreamWriter.write(data);
//            outputStreamWriter.close();
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
//    }
}
