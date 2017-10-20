package com.acfreeman.socialmediascanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Phone;
import com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity;

import java.util.ArrayList;
import java.util.List;


public class RegistrationInformation extends AppCompatActivity {
   // private LinearLayout mLayout;
   // private EditText mEditText;
   // private Button  addEmailBtn;
    //FrameLayout frameLayout = findViewById(R.id.content);
    //mTextMessage = new TextView(this);


    private int width;
    private int height;

    //Create view and view group objects

    //initialize editText objects which are the text boxes that are displayed in the screen
    private EditText curPhone;
    private EditText curEmail;
    private RelativeLayout layout;//initialize the relative layout object

    public ArrayList<EditText> PhoneList = new ArrayList<EditText>();
    public ArrayList<EditText> EmailList = new ArrayList<EditText>();

    public int plusEmailCnt;
    public int plusPhoneCnt;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    SharedPreferences mPrefs;
    final String firstLaunchPref= "firstLaunch";


    public int plus1count = 0;
    public int plus2count = 0;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    //
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_information);

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


        final TableLayout table = (TableLayout) findViewById(R.id.table_main);


        TableRow tableRow;
        TextView t1;
        Switch t2;


        //////
        List texts = new ArrayList();
        LocalDatabase db = new LocalDatabase(getApplicationContext());

        Display display = getWindowManager().getDefaultDisplay(); Point size = new Point(); display.getSize(size); width = size.x; height = size.y;
        final int textWidth = width/2;


        final EditText nameEditText = new EditText(this);
        nameEditText.setHint("Name");
        nameEditText.setWidth(textWidth);
        tableRow = new TableRow(this);
        tableRow.addView(nameEditText);
        table.addView(tableRow);

        ////////phone/////////
        final EditText phoneEditText = new EditText(this);
        phoneEditText.setHint("Phone");
        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneEditText.setWidth(textWidth);

        final ImageButton plusPhone = new ImageButton(this);
//        final Button plusPhone = new Button(this);
//        plusPhone.setText("+");
        plusPhone.setImageResource(R.drawable.ic_add_circle_green_24px);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        plusPhone.setBackgroundResource(typedValue.resourceId);
        plusPhoneCnt = 0;

        final TableRow phoneRow = new TableRow(this);
        phoneRow.addView(phoneEditText);
        phoneRow.addView(plusPhone);
        table.addView(phoneRow);

        PhoneList.add(phoneEditText);
        ////////phone/////////


        ////////email/////////
        EditText emailEditText = new EditText(this);
        emailEditText.setHint("Email");
        emailEditText.setWidth(textWidth);

//        final Button plusEmail = new Button(this);
//        plusEmail.setText("+");
        final ImageButton plusEmail = new ImageButton(this);
        plusEmail.setImageResource(R.drawable.ic_add_circle_green_24px);
        plusEmail.setBackgroundResource(typedValue.resourceId);
        plusEmailCnt = 0;

        TableRow emailRow = new TableRow(this);
        emailRow.addView(emailEditText);
        emailRow.addView(plusEmail);
        table.addView(emailRow);

        EmailList.add(emailEditText);
        ////////email/////////

        final Button submitButton = new Button(this);
        submitButton.setText("Submit");
        TableRow submitRow = new TableRow(this);
        submitRow.addView(submitButton);
        table.addView(submitRow);


        plusPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(plusPhoneCnt<2) {

                    TableRow curRow = (TableRow) view.getParent();
                    // It's index
                    int index = table.indexOfChild(curRow);
                    curRow.removeView(plusPhone);


//                    Button minusPhone = new Button(getApplicationContext());
//                    minusPhone.setText("-");
//                    minusPhone.setWidth(LayoutParams.WRAP_CONTENT);  //Doesn't work
//                    curRow.addView(minusPhone);
//
//                    minusPhone.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                        }
//                    });


                    EditText phoneEditText = new EditText(getApplicationContext());
                    phoneEditText.setHint("Phone");
                    phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                    phoneEditText.setWidth(textWidth);
                    PhoneList.add(phoneEditText);




                    TableRow newPhoneRow = new TableRow(getApplicationContext());
                    newPhoneRow.addView(phoneEditText);
                    newPhoneRow.addView(plusPhone);

                    table.addView(newPhoneRow, index + 1);

                    plusPhoneCnt++;
                }
            }
        });

        plusEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(plusEmailCnt < 2) {


                    TableRow curRow = (TableRow) view.getParent();
                    // It's index
                    int index = table.indexOfChild(curRow);
                    curRow.removeView(plusEmail);

                    EditText emailEditText = new EditText(getApplicationContext());
                    emailEditText.setHint("Email");
                    emailEditText.setWidth(textWidth);
                    EmailList.add(emailEditText);


                    TableRow newEmailRow = new TableRow(getApplicationContext());
                    newEmailRow.addView(emailEditText);
                    newEmailRow.addView(plusEmail);

                    table.addView(newEmailRow, index + 1);

                    plusEmailCnt++;
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;

                if (nameEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Name is required!", Toast.LENGTH_SHORT).show();
                    nameEditText.setError("Name is required!");
                    error = true;
                }



                ////
                    LocalDatabase database = new LocalDatabase(getApplicationContext());
                    Owner owner = new Owner(0, nameEditText.getText().toString());
                    database.addOwner(owner);


                    for (EditText p : PhoneList) {
                        if (p.getText().toString().trim().equals("")) {
                            Toast.makeText(getApplicationContext(), "Phone number is required!", Toast.LENGTH_SHORT).show();
                            p.setError("Phone number is required!");
                            error = true;
                        } else {
                            Phone phone = new Phone(owner.getId(), Long.parseLong(p.getText().toString()), "Cell");
                            database.addPhone(phone);
                        }
                    }

                    for (EditText e : EmailList) {
                        if (e.getText().toString().trim().equals("")) {
                            Toast.makeText(getApplicationContext(), "Email is required!", Toast.LENGTH_SHORT).show();
                            e.setError("Email is required!");
                            error = true;
                        } else {
                            Email email = new Email((long)owner.getId(), e.getText().toString(), "Work");
                            database.addEmail(email);
                        }
                    }
                    ////

                    if(!error) {
                        Intent startIntent = new Intent(getApplicationContext(), SocialMediaLoginActivity.class);
                        startActivity(startIntent);
                    }


            }
        });


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

}
