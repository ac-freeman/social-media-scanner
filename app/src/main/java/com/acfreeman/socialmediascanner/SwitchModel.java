package com.acfreeman.socialmediascanner;

import android.widget.ImageView;
import android.widget.Switch;

import com.acfreeman.socialmediascanner.db.Emails;
import com.acfreeman.socialmediascanner.db.Phones;
import com.acfreeman.socialmediascanner.db.Social;

import java.util.ArrayList;

/**
 * Created by Andrew on 10/13/2017.
 */

public class SwitchModel {

    String switchName;
    String tag;
    int switchImage;
    Boolean state;
    Switch switcher;
    String user_id; //not used for phones/emails switches

    public SwitchModel(String switchName, String tag, int switchImage) {
        this.switchName = switchName;
        this.tag = tag;
        this.switchImage = switchImage;
        this.state = false;
    }
    public SwitchModel(String switchName, String tag, int switchImage, String user_id) {
        this.switchName = switchName;
        this.tag = tag;
        this.switchImage = switchImage;
        this.state = false;
        this.user_id = user_id;
    }

    public void toggleState(){
        this.state = !state;
    }
    public void setSwitcher(Switch switcher) {
        this.switcher = switcher;
    }
    public Switch getSwitcher() {return switcher;}

    public void setState(Boolean state){
        this.state = state;
    }

    public boolean getState(){
        return state;
    }

    public void setSwitchName(String switchName) {this.switchName = switchName;}

    public String getSwitchName() {
        return switchName;
    }

    public int getSwitchImage(){
        return switchImage;
    }

    public String getTag() {return tag;}

    public String getUser_id() {return user_id;}



}
