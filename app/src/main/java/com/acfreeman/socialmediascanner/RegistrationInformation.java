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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
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

import static com.acfreeman.socialmediascanner.MainActivity.firstProfileCreationPref;


public class RegistrationInformation extends AppCompatActivity {

    private int width;
    private int height;

    public ArrayList<PhoneRow> PhoneRowList = new ArrayList<>();
    public ArrayList<EmailRow> EmailRowList = new ArrayList<>();

    public int plusEmailCnt;
    public int plusPhoneCnt;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    SharedPreferences mPrefs;

    int textWidth;

    String caller;
    Class callerClass;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    //
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_information);

        caller = getIntent().getStringExtra("caller");
        if (caller != null) {
            try {
                callerClass = Class.forName(caller);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        final TableLayout table = (TableLayout) findViewById(R.id.table_main);


        final TableRow tableRow = (TableRow) this.getLayoutInflater().inflate(R.layout.row_item_registration_name, null,false);

        final EditText nameEditText = tableRow.findViewById(R.id.edit_text);
        nameEditText.setHint("Name");

        //////
        List texts = new ArrayList();
        LocalDatabase db = new LocalDatabase(getApplicationContext());

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        textWidth = (width * 9) / 10;

//
        LocalDatabase database = new LocalDatabase(getApplicationContext());
        Owner owner = database.getOwner(0);

        if (owner.getName() != null) {
            nameEditText.setText(owner.getName());
        }
        nameEditText.setWidth(textWidth);
        table.addView(tableRow);

        ////////phone/////////
        final ArrayList<Phone> phones = database.getUserPhones(0);
        int index = 1;
        if (phones.size() > 0) {
            for (int i = 0; i < phones.size(); i++) {
                addPhoneEditText(table, index, phones.get(i).getNumber(), phones.get(i).getType());
                index++;
            }
        }

        addPhoneEditText(table, index, -1);
        index++;

        plusPhoneCnt = 0;
        ////////phone/////////


        ////////email/////////
        final ArrayList<Email> emails = database.getUserEmails(0);
        if (emails.size() > 0) {
            for (int i = 0; i < emails.size(); i++) {
                addEmailEditText(table, index, emails.get(i).getEmail(), emails.get(i).getType());
                index++;
            }
        }
        addEmailEditText(table, index, "");
        index++;

        plusEmailCnt = 0;
        //////email/////////

        final TableRow submitRow = (TableRow) this.getLayoutInflater().inflate(R.layout.row_item_registration_button, null,false);
        table.addView(submitRow);
        Button submitButton = submitRow.findViewById(R.id.submit_button);


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

                for (PhoneRow p : PhoneRowList) {

                    matcher1 = Pattern.compile(validPhone).matcher(p.getEditText().getText().toString());
                    matcher2 = Pattern.compile(validInterPhone).matcher(p.getEditText().getText().toString());

                    number = p.getEditText().getText().toString();
                    numFormated = number.replaceAll("[^0-9]", "");
                    if (numFormated.length() < 7 || numFormated.length() > 7 && !matcher1.matches() && !matcher2.matches()) {
                        if (numFormated.length() != 0) {
                            Toast.makeText(getApplicationContext(), "Phone number is not valid!", Toast.LENGTH_SHORT).show();

                            if (numFormated.length() > 10) {
                                p.getEditText().setError("For International Numbers Use (+). US Country Code Not Needed.");
                            } else {
                                p.getEditText().setError("Enter a vaild phone number!");
                            }
                            error = true;
                        } else {
                            p.getEditText().setTag("DELETE");
                        }
                    }
                }
                for (int i = 0; i < PhoneRowList.size(); i++) {
                    if (PhoneRowList.get(i).getEditText().getTag() != null) {
                        if (PhoneRowList.get(i).getEditText().getTag().equals("DELETE")) {
                            PhoneRowList.remove(i);
                            i--;
                        }
                    }
                }

                for (EmailRow e : EmailRowList) {
                    matcher1 = Pattern.compile(validEmail).matcher(e.getEditText().getText().toString());
                    Log.i("Email Debug", "Email address: " + e.getEditText().getText().toString());
                    if (e.getEditText().getText().length() != 0) {
                        if (!matcher1.matches()) {
                            Toast.makeText(getApplicationContext(), "Email address is not valid!", Toast.LENGTH_LONG).show();
                            e.getEditText().setError("Enter valid email address!");
                            error = true;
                        }
                    } else {
                        e.getEditText().setTag("DELETE");
                    }
                }
                for (int i = 0; i < EmailRowList.size(); i++) {
                    if (EmailRowList.get(i).getEditText().getTag() != null) {
                        if (EmailRowList.get(i).getEditText().getTag().equals("DELETE")) {
                            EmailRowList.remove(i);
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
                    if (phones.size() > 0) {
                        database.deleteUserPhones(owner.getId());
                    }


                    for (PhoneRow p : PhoneRowList) {
                        //matcher= Pattern.compile(validPhone).matcher(p.getText().toString());
                        number = p.getEditText().getText().toString();
                        numFormated = number.replaceAll("[^0-9]", "");
                        String type = p.getSpinner().getSelectedItem().toString();
                        Phone phone = new Phone(owner.getId(), Long.parseLong(numFormated), type);
                        Log.i("PHONEDEBUG", String.valueOf(phone.getNumber()));
                        database.addPhone(phone);
                        Toast.makeText(getApplicationContext(), "Phone number stored as: " + numFormated, Toast.LENGTH_SHORT).show();
                    }

                    for (EmailRow e : EmailRowList) {

                        String type = e.getSpinner().getSelectedItem().toString();
                        Email email = new Email((long) owner.getId(), e.getEditText().getText().toString(), type);
                        database.addEmail(email);
                        Toast.makeText(getApplicationContext(), "Email stored as: " + email.getEmail(), Toast.LENGTH_SHORT).show();
                    }


                    if (callerClass != null && callerClass.getName().equals("com.acfreeman.socialmediascanner.social.SocialMediaLoginActivity")) {
                        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean(firstProfileCreationPref, false);
                        editor.commit(); // Very important to save the preference
                    }

                    Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(startIntent);
                }

            }
        });
    }


    private void addPhoneEditText(final TableLayout table, final int index, long number) {
        addPhoneEditText(table, index, number, "Cell");
    }

    private void addPhoneEditText(final TableLayout table, final int index, long number, String type) {

        final TableRow phoneRow = (TableRow) this.getLayoutInflater().inflate(R.layout.row_item_registration, null,false);

        final ImageView image = phoneRow.findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_phone_black_24dp);
        if(PhoneRowList.size()==0){
            image.setVisibility(View.VISIBLE);
        }

        final EditText phoneEditText = phoneRow.findViewById(R.id.edit_text);
        phoneEditText.setHint("Phone");
        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);

        phoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phoneEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});

        final Spinner spinner = phoneRow.findViewById(R.id.spinner);
        String[] spinnerList = new String[]{"Cell","Work","Home","Other"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(spinnerArrayAdapter.getPosition(type));

        final ImageButton phoneButton = phoneRow.findViewById(R.id.row_button);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        phoneButton.setBackgroundResource(typedValue.resourceId);


        if (number != -1) {
            phoneEditText.setText(String.valueOf(number));
            phoneButton.setImageResource(R.drawable.ic_remove_circle_red_24dp);
            phoneButton.setTag("remove");
        } else {

            phoneButton.setImageResource(R.drawable.ic_add_circle_green_24px);
            phoneButton.setTag("add");
        }

        table.addView(phoneRow, index);

        final PhoneRow row = new PhoneRow(phoneEditText,spinner, image);
        PhoneRowList.add(row);


        phoneButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                if (phoneButton.getTag().equals("add")) {
                    addPhoneEditText(table, table.indexOfChild(phoneRow) + 1, -1);
                    phoneButton.setImageResource(R.drawable.ic_remove_circle_red_24dp);/////
                    phoneButton.setTag("remove");

                    plusPhoneCnt++;
                } else {
                    table.removeView(phoneRow);
                    PhoneRowList.remove(row);
                    PhoneRowList.get(0).getImage().setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void addEmailEditText(final TableLayout table, final int index, String text) {
        addEmailEditText(table, index, text, "Personal");
    }

    private void addEmailEditText(final TableLayout table, final int index, String text, String type) {

        final TableRow emailRow = (TableRow) this.getLayoutInflater().inflate(R.layout.row_item_registration, null,false);


        final ImageView image = emailRow.findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_email_black_24dp);
        if(EmailRowList.size()==0){
            image.setVisibility(View.VISIBLE);
        }
        final EditText emailEditText = emailRow.findViewById(R.id.edit_text);
        emailEditText.setHint("Email");
        emailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        final Spinner spinner = emailRow.findViewById(R.id.spinner);
        String[] spinnerList = new String[]{"Personal","Work","School","Other"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(spinnerArrayAdapter.getPosition(type));

        if (!text.equals(""))
            emailEditText.setText(text);

        final ImageButton emailButton = emailRow.findViewById(R.id.row_button);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        emailButton.setBackgroundResource(typedValue.resourceId);

        if (!text.equals("")) {
            emailEditText.setText(text);
            emailButton.setImageResource(R.drawable.ic_remove_circle_red_24dp);
            emailButton.setTag("remove");
        } else {

            emailButton.setImageResource(R.drawable.ic_add_circle_green_24px);
            emailButton.setTag("add");
        }

        table.addView(emailRow, index);

        final EmailRow row = new EmailRow(emailEditText,spinner,image);
        EmailRowList.add(row);

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (emailButton.getTag().equals("add")) {
                    addEmailEditText(table, table.indexOfChild(emailRow) + 1, "");
                    emailButton.setImageResource(R.drawable.ic_remove_circle_red_24dp);/////
                    emailButton.setTag("remove");

                    plusEmailCnt++;
                } else {
                    table.removeView(emailRow);
                    EmailRowList.remove(row);
                    EmailRowList.get(0).getImage().setVisibility(View.VISIBLE);
                }

            }
        });

    }

    private class PhoneRow{

        EditText editText;
        Spinner spinner;
        ImageView image;

        PhoneRow(EditText editText, Spinner spinner, ImageView image){
            this.editText = editText;
            this.spinner = spinner;
            this.image = image;
        }

        public EditText getEditText() {
            return editText;
        }
        public Spinner getSpinner() {
            return spinner;
        }
        public ImageView getImage() {return image;}
    }

    private class EmailRow{

        EditText editText;
        Spinner spinner;
        ImageView image;

        EmailRow(EditText editText, Spinner spinner, ImageView image){
            this.editText = editText;
            this.spinner = spinner;
            this.image = image;
        }

        public EditText getEditText() {
            return editText;
        }
        public Spinner getSpinner() {
            return spinner;
        }
        public ImageView getImage() {return image;}
    }
//
}
