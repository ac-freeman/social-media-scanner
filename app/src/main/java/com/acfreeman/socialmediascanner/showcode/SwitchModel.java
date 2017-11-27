package com.acfreeman.socialmediascanner.showcode;

import android.widget.Switch;

import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.Phone;

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
    Phone phone;
    Email email;

    public SwitchModel(String switchName, String tag, int switchImage) {
        this.switchName = switchName;
        this.tag = tag;
        this.switchImage = switchImage;
        this.state = false;
    }

    //phone constructor
    public SwitchModel(String switchName, String tag, int switchImage, Phone phone) {
        this.switchName = switchName;
        this.tag = tag;
        this.switchImage = switchImage;
        this.state = false;
        this.phone = phone;
    }

    //email constructor
    public SwitchModel(String switchName, String tag, int switchImage, Email email) {
        this.switchName = switchName;
        this.tag = tag;
        this.switchImage = switchImage;
        this.state = false;
        this.email = email;
    }

    public SwitchModel(String tag, String user_id) {
        this.tag = tag;
        this.state = false;
        this.user_id = user_id;
        setSwitchAttribs_fromTag(tag);
    }

    private void setSwitchAttribs_fromTag(String tag){
        switch (tag){
            case "tw":
                this.switchName = "Twitter";
                this.switchImage = R.drawable.icons8_twitter;
                break;
            case "li":
                this.switchName = "LinkedIn";
                this.switchImage = R.drawable.icons8_linkedin;
                break;
            case "sp":
                this.switchName = "Spotify";
                this.switchImage = R.drawable.ic_spotify_24dp;
                break;
            case "fb":
                this.switchName = "Facebook";
                this.switchImage = R.drawable.fb_24dp;
                break;
            case "go":
                this.switchName = "Google+";
                this.switchImage = R.drawable.google_plus_24dp;
                break;

            default:
                this.switchName = "ERR.";
                break;
        }
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

    public Phone getPhone() {return phone;}
    public Email getEmail() {return email;}



}
