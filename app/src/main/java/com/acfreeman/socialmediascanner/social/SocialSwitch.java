package com.acfreeman.socialmediascanner.social;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Switch;

import com.acfreeman.socialmediascanner.R;

/**
 * Created by Andrew on 9/25/2017.
 */

public class SocialSwitch {
    private String type_db; //name in the database
    private String type_name;   //official name ("WhatsApp","LinkedIn")
    private String user_id;
    private int image_res;
    private Switch sw;
    private boolean enabled;

    public SocialSwitch()
    {
    }
    public SocialSwitch(String type_db, String user_id, Context context)
    {
        this.type_db=type_db;
        this.user_id=user_id;
        this.enabled = false;
        setType_name(type_db);

        this.sw = new Switch(context);
        sw.setText(getType_name());
//        this.sw.setText("TEST");

    }
    private void setType_name(String type_db){
        switch (type_db){
            case "tw":
                this.type_name = "Twitter";
                this.image_res = R.drawable.twitter_logo_blue;
                break;
            case "li":
                this.type_name = "LinkedIn";
                break;
            default:
                this.type_name = "ERR.";
        }
    }

    public void toggleEnabled() {
        this.enabled = !this.enabled;
    }

    public String getType_db() {return type_db;}
    public String getType_name() {return type_name;}
    public String getUser_id() {return user_id;}
    public int getImage_res() {return image_res;}
    public boolean getEnabled() {return enabled;}

    public Switch getSwitch() {return sw;}
    public Switch setSwitch(Switch sw) {
        this.sw = sw;
        sw.setText(getType_name());
        return sw;
    }
}
