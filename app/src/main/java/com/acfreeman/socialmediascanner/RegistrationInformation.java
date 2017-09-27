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
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.acfreeman.socialmediascanner.db.Emails;
import com.acfreeman.socialmediascanner.db.LocalDatabase;
import com.acfreeman.socialmediascanner.db.Owner;
import com.acfreeman.socialmediascanner.db.Phones;
import com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity;

import java.util.ArrayList;


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



    //not sure what this does - does something when the app is first launched it think?
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


        //get screen dimensions in px
        Display display = getWindowManager().getDefaultDisplay(); Point size = new Point(); display.getSize(size); width = size.x; height = size.y;

        layout = new RelativeLayout(this);//initialzing the relative layout object

        //Defining the layout programmatically
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);

        //This is the dimensions for the view and view group objects
        RelativeLayout.LayoutParams nameParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams phoneParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final RelativeLayout.LayoutParams emailParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final RelativeLayout.LayoutParams buttonParam1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final RelativeLayout.LayoutParams buttonParam2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams submitParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        //name box
        final EditText editName = createEditText("Name", nameParam);

        nameParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);


        //phone box
        final EditText editPhone = createEditText("Phone", phoneParam);
        editPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneParam.addRule(RelativeLayout.BELOW, editName.getId());
        PhoneList.add(editPhone);




        final EditText editEmail = createEditText("Email", emailParam);
        emailParam.addRule(RelativeLayout.BELOW, editPhone.getId());
        EmailList.add(editEmail);


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
                boolean error = false;

                if (editName.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Name is required!", Toast.LENGTH_SHORT).show();
                    editName.setError("Name is required!");
                    error = true;
                }



                ////
                    LocalDatabase database = new LocalDatabase(getApplicationContext());
                    Owner owner = new Owner(0, editName.getText().toString());
                    database.addOwner(owner);


                    for (EditText p : PhoneList) {
                        if (p.getText().toString().trim().equals("")) {
                            Toast.makeText(getApplicationContext(), "Phone number is required!", Toast.LENGTH_SHORT).show();
                            p.setError("Phone number is required!");
                            error = true;
                        } else {
                            Phones phone = new Phones(owner.getId(), Integer.parseInt(p.getText().toString()), "Cell");
                            database.addPhones(phone);
                        }
                    }

                    for (EditText e : EmailList) {
                        if (e.getText().toString().trim().equals("")) {
                            Toast.makeText(getApplicationContext(), "Email is required!", Toast.LENGTH_SHORT).show();
                            e.setError("Email is required!");
                            error = true;
                        } else {
                            Emails email = new Emails(owner.getId(), e.getText().toString(), "Work");
                            database.addEmails(email);
                        }
                    }
                    ////

                    if(!error) {
                        Intent startIntent = new Intent(getApplicationContext(), SocialMediaLoginActivity.class);
                        startActivity(startIntent);
                    }


            }
        });

        plus1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {

                if(plus1count < 2) {
                    RelativeLayout.LayoutParams newPhoneParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    EditText newPhone = createEditText("Phone", newPhoneParam);
                    PhoneList.add(newPhone);
                    newPhoneParam.addRule(RelativeLayout.BELOW, curPhone.getId());
                    newPhone.setInputType(InputType.TYPE_CLASS_PHONE);

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

                plus1count++;
                }
            }
        });


        plus2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {


                if(plus2count < 2) {
                    RelativeLayout.LayoutParams newEmailParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    EditText newEmail = createEditText("Email", newEmailParam);
                    EmailList.add(newEmail);

                    newEmailParam.addRule(RelativeLayout.BELOW, curPhone.getId());

                    layout.removeView(plus2);
                    buttonParam2.addRule(RelativeLayout.BELOW, curEmail.getId());
                    newEmailParam.addRule(RelativeLayout.BELOW, curEmail.getId());
                    curEmail = newEmail;
                    layout.addView(newEmail, newEmailParam);
                    layout.addView(plus2, buttonParam2);

                    plus2count++;

                }
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

}
