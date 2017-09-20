package com.acfreeman.socialmediascanner;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
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
    @Override
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

        layout.addView(editName, nameParam);
        layout.addView(editPhone, phoneParam);
        layout.addView(editEmail, emailParam);
        layout.addView(plus1, buttonParam1);
        layout.addView(plus2, buttonParam2);

        curPhone = editPhone;
        curEmail = editEmail;

        plus1.setOnClickListener(new View.OnClickListener() {
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


        setContentView(layout);

//        RelativeLayout relativelayout = new RelativeLayout(this);
//        Button btn = new Button(this);
//        EditText nameEditText = new EditText(this);
//        nameEditText.setId(0);
//        nameEditText.setHint("Name");
//
//        LayoutParams layoutparams = new LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT
//        );
//
//        LayoutParams LayoutParamsEditText = new LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT
//        );
//
//        LayoutParams LayoutParamsButtons = new LayoutParams(
//                LayoutParams.WRAP_CONTENT,
//                LayoutParams.WRAP_CONTENT
//        );
//        relativelayout.setLayoutParams(layoutparams);
//
//        LayoutParamsEditText.addRule(RelativeLayout.CENTER_IN_PARENT);
//        LayoutParamsButtons.addRule(RelativeLayout.CENTER_IN_PARENT);
//
////        nameEditText.setLayoutParams(LayoutParamsEditText);
//        LayoutParamsButtons.addRule(RelativeLayout.BELOW, nameEditText.getId());
//        btn.setLayoutParams(LayoutParamsButtons);
//        btn.setText("Button Name");
//        btn.setBackgroundColor(Color.BLUE);
//        relativelayout.addView(nameEditText, LayoutParamsEditText);
//
//        relativelayout.addView(btn, LayoutParamsButtons);
//        setContentView(relativelayout);




//        setContentView(R.layout.activity_registration_information);
//
//        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.activity_registration_information_layout);
//
//
//
//
//        Button nextBtn = (Button) findViewById(R.id.nextBtn);
//        nextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(startIntent);
//            }
//        });
//        //mLayout = (LinearLayout) findViewById(R.id.info);
//        mEditText = (EditText) findViewById(R.id.emailEditText);
//        addEmailBtn = (Button) findViewById(R.id.addEmaiBtn);
//        addEmailBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditText textView = new EditText(RegistrationInformation.this);
//                textView.setText("Email");
//
//
//                constraintLayout.addView(textView);
//
//
//                constraintSet = new ConstraintSet();
//                constraintSet.clone(constraintLayout);
//
//                constraintSet.connect(mEditText.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.RIGHT, 0);
//                constraintSet.constrainDefaultHeight(mEditText.getId(), 200);
//                constraintSet.applyTo(constraintLayout);
//            }
//        });

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
