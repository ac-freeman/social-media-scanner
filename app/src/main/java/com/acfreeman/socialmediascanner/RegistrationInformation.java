package com.acfreeman.socialmediascanner;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;


public class RegistrationInformation extends AppCompatActivity {
    private LinearLayout mLayout;
    private EditText mEditText;
    private Button  addEmailBtn;
    //FrameLayout frameLayout = findViewById(R.id.content);
    //mTextMessage = new TextView(this);

    private int width;
    private int height;
    private EditText curPhone;
    private EditText curEmail;
    private RelativeLayout layout;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    SharedPreferences mPrefs;
    final String firstLaunchPref= "firstLaunch";

    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //get screen dimensions in px
        Display display = getWindowManager().getDefaultDisplay(); Point size = new Point(); display.getSize(size); width = size.x; height = size.y;

        layout = new RelativeLayout(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams nameParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams phoneParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final RelativeLayout.LayoutParams emailParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final RelativeLayout.LayoutParams buttonParam1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final RelativeLayout.LayoutParams buttonParam2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams submitParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        //name box
        EditText editName = createEditText("Name", nameParam);
        nameParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);


        //phone box
        EditText editPhone = createEditText("Phone", phoneParam);
        phoneParam.addRule(RelativeLayout.BELOW, editName.getId());



        EditText editEmail = createEditText("Email", emailParam);
        emailParam.addRule(RelativeLayout.BELOW, editPhone.getId());

        //first plus button
        final Button plus1 = createPlusButton(buttonParam1, editPhone);
        buttonParam1.addRule(RelativeLayout.BELOW, editName.getId());

        //second plus button
        final Button plus2 = createPlusButton(buttonParam2, editEmail);
        buttonParam2.addRule(RelativeLayout.BELOW, editPhone.getId());

        //submit button
        Button submit = new Button(this);
        submit.setText("Submit");
        submitParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        submitParam.addRule(RelativeLayout.CENTER_HORIZONTAL);

        layout.addView(editName, nameParam);
        layout.addView(editPhone, phoneParam);
        layout.addView(editEmail, emailParam);
        layout.addView(plus1, buttonParam1);
        layout.addView(plus2, buttonParam2);
        layout.addView(submit, submitParam);

        curPhone = editPhone;
        curEmail = editEmail;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), SocialMediaLoginActivity.class);
                startActivity(startIntent);
            }
        });

        plus1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                RelativeLayout.LayoutParams newPhoneParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                EditText newPhone = createEditText("Phone", newPhoneParam);
                newPhoneParam.addRule(RelativeLayout.BELOW, curPhone.getId());

                layout.removeView(plus1);
                buttonParam1.addRule(RelativeLayout.BELOW, curPhone.getId());
                curPhone = newPhone;
                layout.addView(newPhone, newPhoneParam);
                layout.addView(plus1, buttonParam1);

                layout.removeView(curEmail);
                layout.removeView(plus2);
                buttonParam2.addRule(RelativeLayout.BELOW, curPhone.getId());
                emailParam.addRule(RelativeLayout.BELOW, curPhone.getId());
                layout.addView(curEmail, emailParam);
                layout.addView(plus2, buttonParam2);

            }
        });


        plus2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                RelativeLayout.LayoutParams newEmailParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                EditText newEmail = createEditText("Email", newEmailParam);
                newEmailParam.addRule(RelativeLayout.BELOW, curPhone.getId());

                layout.removeView(plus2);
                buttonParam2.addRule(RelativeLayout.BELOW, curEmail.getId());
                newEmailParam.addRule(RelativeLayout.BELOW, curEmail.getId());
                curEmail = newEmail;
                layout.addView(newEmail, newEmailParam);
                layout.addView(plus2, buttonParam2);

            }
        });

        setContentView(layout);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // second argument is the default to use if the preference can't be found
        Boolean firstLaunch = mPrefs.getBoolean(firstLaunchPref, true);

        if(firstLaunch) {

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(firstLaunchPref, false);
        editor.commit(); // Very important to save the preference
        }

        else {
        Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
         startActivity(startIntent);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private EditText createEditText(String text, LayoutParams param) {
        //name box
        EditText edit = new EditText(this);
        edit.setId(View.generateViewId());
        edit.setHint(text);
        //param
        param.addRule(RelativeLayout.CENTER_HORIZONTAL);
        param.width = width/2;

        return edit;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Button createPlusButton(LayoutParams param, EditText edit) {
        Button button = new Button(this);
        button.setId(View.generateViewId());
        button.setText("+");
        param.addRule(RelativeLayout.RIGHT_OF, edit.getId());


        return button;
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
