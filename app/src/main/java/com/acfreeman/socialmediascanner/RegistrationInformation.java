package com.acfreeman.socialmediascanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    final String firstLaunchPref = "firstLaunch";


    public int plus1count = 0;
    public int plus2count = 0;
    int textWidth;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    //
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_information);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

//        // second argument is the default to use if the preference can't be found
//        Boolean firstLaunch = mPrefs.getBoolean(firstLaunchPref, true);
//
//        if(firstLaunch) {
//
//        SharedPreferences.Editor editor = mPrefs.edit();
//        editor.putBoolean(firstLaunchPref, false);
//        editor.commit(); // Very important to save the preference
//        }
//
//        else {
//        Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
//            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//         startActivity(startIntent);
//        }


        final TableLayout table = (TableLayout) findViewById(R.id.table_main);


        TableRow tableRow;
        TextView t1;
        Switch t2;


        //////
        List texts = new ArrayList();
        LocalDatabase db = new LocalDatabase(getApplicationContext());

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        textWidth = (width * 3) / 4;

        LocalDatabase database = new LocalDatabase(getApplicationContext());
        Owner owner = database.getOwner(0);

        final EditText nameEditText = new EditText(this);
        if(owner.getName() != null){
            nameEditText.setText(owner.getName());
        }
        nameEditText.setHint("Name");
        nameEditText.setWidth(textWidth);
        tableRow = new TableRow(this);
        tableRow.addView(nameEditText);
        table.addView(tableRow);

        ////////phone/////////


        final ArrayList<Phone> phones = database.getUserPhones(0);
        int index = 1;
        if (phones.size() > 0) {
            for (int i = 0; i <phones.size() ; i++) {
                addPhoneEditText(table,index,phones.get(i).getNumber());
                index++;
            }
        }

        addPhoneEditText(table,index, -1);
        index++;

        plusPhoneCnt = 0;
        ////////phone/////////


        ////////email/////////

        final ArrayList<Email> emails = database.getUserEmails(0);
        if (emails.size() > 0) {
            for (int i = 0; i <emails.size() ; i++) {
                addEmailEditText(table,index,emails.get(i).getEmail());
                index++;
            }
        }

        addEmailEditText(table,index, "");
        index++;

        plusEmailCnt = 0;
        ////////email/////////

        final Button submitButton = new Button(this);
        submitButton.setText("Submit");
        TableRow submitRow = new TableRow(this);
        submitRow.addView(submitButton);
        table.addView(submitRow);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;
                Matcher matcher1;
                Matcher matcher2;
                String number;
                String numFormated;
                String validEmail = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +

                        "\\@" +

                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +

                        "(" +

                        "\\." +

                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +

                        ")+";

                String validPhone = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$";
                String validInterPhone = "^\\+(?:[0-9] ?){6,14}[0-9]$";

                if (nameEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Name is required!", Toast.LENGTH_SHORT).show();
                    nameEditText.setError("Name is required!");
                    error = true;
                }

                ////

                for (EditText p : PhoneList) {

                    matcher1 = Pattern.compile(validPhone).matcher(p.getText().toString());
                    matcher2 = Pattern.compile(validInterPhone).matcher(p.getText().toString());

                    number = p.getText().toString();
                    numFormated = number.replaceAll("[^0-9]", "");
                     if (numFormated.length() < 7 || numFormated.length() > 7 && !matcher1.matches() && !matcher2.matches()) {
                         if(numFormated.length() != 0) {
                             Toast.makeText(getApplicationContext(), "Phone number is not valid!", Toast.LENGTH_SHORT).show();

                             if (numFormated.length() > 10) {
                                 p.setError("For International Numbers Use (+). US Country Code Not Needed.");
                             } else {
                                 p.setError("Enter a vaild phone number!");
                             }
                             error = true;
                         } else{
                             p.setTag("DELETE");
                         }
                    }
                }
                for (int i = 0; i < PhoneList.size(); i++) {
                    if(PhoneList.get(i).getTag() != null) {
                        if (PhoneList.get(i).getTag().equals("DELETE")) {
                            PhoneList.remove(i);
                            i--;
                        }
                    }
                }

                for (EditText e : EmailList) {
                    matcher1 = Pattern.compile(validEmail).matcher(e.getText().toString());
                    Log.i("Email Debug", "Email address: " + e.getText().toString());
                    if(e.getText().length()!= 0) {
                        if (!matcher1.matches()) {
                            Toast.makeText(getApplicationContext(), "Email address is not valid!", Toast.LENGTH_LONG).show();
                            e.setError("Enter valid email address!");
                            error = true;
                        }
                    } else {
                        e.setTag("DELETE");
                    }
                }
                for (int i = 0; i < EmailList.size(); i++) {
                    if(EmailList.get(i).getTag() != null) {
                        if (EmailList.get(i).getTag().equals("DELETE")) {
                            EmailList.remove(i);
                            i--;
                        }
                    }
                }
                ////

                if (!error) {
                    LocalDatabase database = new LocalDatabase(getApplicationContext());
                    Owner owner = database.getOwner(0);
                    database.deleteOwner(owner);
                    owner = new Owner(0, nameEditText.getText().toString());
                    database.addOwner(owner);

                    final ArrayList<Email> emails = database.getUserEmails(0);
                    if (emails.size() > 0) {
                        database.deleteUserEmails(owner.getId());
                    }

                    final ArrayList<Phone> phones = database.getUserPhones(0);
                    if(phones.size() > 0) {
                        database.deleteUserPhones(owner.getId());
                    }


                    for (EditText p : PhoneList) {
                        //matcher= Pattern.compile(validPhone).matcher(p.getText().toString());
                        number = p.getText().toString();
                        numFormated = number.replaceAll("[^0-9]", "");
                        Phone phone = new Phone(owner.getId(), Long.parseLong(numFormated), "Cell");
                        Log.i("PHONEDEBUG",String.valueOf(phone.getNumber()));
                        database.addPhone(phone);
                        Toast.makeText(getApplicationContext(), "Phone number stored as: " + numFormated, Toast.LENGTH_SHORT).show();
                    }

                    for (EditText e : EmailList) {

                        Email email = new Email((long) owner.getId(), e.getText().toString(), "Work");
                        database.addEmail(email);
                        Toast.makeText(getApplicationContext(), "Email stored as: " + email.getEmail(), Toast.LENGTH_SHORT).show();
                    }


                    Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(startIntent);
                }

            }
        });


    }

    private void addPhoneEditText(final TableLayout table, final int index, long number) {
        final EditText phoneEditText = new EditText(this);
        phoneEditText.setHint("Phone");
        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneEditText.setWidth(textWidth);
        phoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phoneEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});

        final ImageButton phoneButton = new ImageButton(this);
//        final Button plusPhone = new Button(this);
//        plusPhone.setText("+");
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        phoneButton.setBackgroundResource(typedValue.resourceId);


        if(number !=-1) {
            phoneEditText.setText(String.valueOf(number));
            phoneButton.setImageResource(R.drawable.ic_remove_circle_red_24dp);
            phoneButton.setTag("remove");
        } else {

            phoneButton.setImageResource(R.drawable.ic_add_circle_green_24px);
            phoneButton.setTag("add");
        }


        final TableRow phoneRow = new TableRow(this);
        phoneRow.addView(phoneEditText);
        phoneRow.addView(phoneButton);
        table.addView(phoneRow, index);

        PhoneList.add(phoneEditText);


        phoneButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                if(phoneButton.getTag().equals("add")) {
                    addPhoneEditText(table, table.indexOfChild(phoneRow)+1, -1);
                    phoneButton.setImageResource(R.drawable.ic_remove_circle_red_24dp);/////
                    phoneButton.setTag("remove");

                    plusPhoneCnt++;
                } else {
                    table.removeView(phoneRow);
                    PhoneList.remove(phoneEditText);
                }

            }
        });

    }

    private void addEmailEditText(final TableLayout table, final int index, String text){
        final EditText emailEditText = new EditText(this);
        emailEditText.setHint("Email");
        emailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
      

        if(!text.equals(""))
            emailEditText.setText(text);
        emailEditText.setWidth(textWidth);
//        final Button plusEmail = new Button(this);
//        plusEmail.setText("+");
        final ImageButton emailButton = new ImageButton(this);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        emailButton.setBackgroundResource(typedValue.resourceId);

        if(!text.equals("")) {
            emailEditText.setText(text);
            emailButton.setImageResource(R.drawable.ic_remove_circle_red_24dp);
            emailButton.setTag("remove");
        } else {

            emailButton.setImageResource(R.drawable.ic_add_circle_green_24px);
            emailButton.setTag("add");
        }


        final TableRow emailRow = new TableRow(this);
        emailRow.addView(emailEditText);
        emailRow.addView(emailButton);
        table.addView(emailRow, index);

        EmailList.add(emailEditText);


        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(emailButton.getTag().equals("add")) {
                    addEmailEditText(table, table.indexOfChild(emailRow)+1,"");
                    emailButton.setImageResource(R.drawable.ic_remove_circle_red_24dp);/////
                    emailButton.setTag("remove");

                    plusEmailCnt++;
                } else {
                    table.removeView(emailRow);
                    EmailList.remove(emailEditText);
                }

            }
        });

    }

}
