package com.acfreeman.socialmediascanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class QrModesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_modes);
        Switch allSwitch = (Switch) findViewById(R.id.switchAll);
        allSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){

                    Toast.makeText(QrModesActivity.this, "All QR toggles turned on", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(QrModesActivity.this, "Toggle turned off", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Step 1: Add option to toggle all information

    //Step 2: Add option to toggle information for a business setting

}
