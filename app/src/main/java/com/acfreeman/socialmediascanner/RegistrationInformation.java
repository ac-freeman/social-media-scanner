package com.acfreeman.socialmediascanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegistrationInformation extends AppCompatActivity {
    private LinearLayout mLayout;
    private EditText mEditText;
    private Button  addEmailBtn;
    //FrameLayout frameLayout = findViewById(R.id.content);
    //mTextMessage = new TextView(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_information);

        //LinearLayout linearLayout = (LinearLayout)findViewById(R.id.info);

//        TextView txt1 = new TextView(RegistrationInformation.this);
//        linearLayout.setBackgroundColor(Color.TRANSPARENT);
//        linearLayout.addView(txt1);


        //LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout2);
//
//        mEditText = (EditText) findViewById(R.id.editText);
//        mButton = (Button) findViewById(R.id.button);


        Button nextBtn = (Button) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntent);
            }
        });
        mLayout = (LinearLayout) findViewById(R.id.info);
        mEditText = (EditText) findViewById(R.id.emailEditText);
        addEmailBtn = (Button) findViewById(R.id.addEmaiBtn);
        addEmailBtn.setOnClickListener(onClick());
        EditText textView = new EditText(this);
        textView.setText("Email");
    }
    private View.OnClickListener onClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.addView(createNewTextView(mEditText.getText().toString()));
            }
        };
    }

    private TextView createNewTextView(String text) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText("Email Adress " + text);
        return textView;
    }

}
