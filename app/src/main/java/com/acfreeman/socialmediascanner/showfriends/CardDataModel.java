package com.acfreeman.socialmediascanner.showfriends;

import android.util.Log;

import com.acfreeman.socialmediascanner.R;
import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.Phone;
import com.acfreeman.socialmediascanner.db.Social;

import java.util.ArrayList;

/**
 * Created by Andrew on 10/20/2017.
 */

public class CardDataModel{


    Phone phone;
    Email email;
    Social social;
    int image;
    char tag;

    public CardDataModel(Phone phone){
        this.phone = phone;
        this.tag = 'p';
        setImage();
    }

    public CardDataModel(Email email){
        this.email = email;
        this.tag = 'e';
        setImage();
    }

    public CardDataModel(Social social){
        this.social= social;
        this.tag = 's';
        setImage();
    }

    private void setImage(){
            switch (tag){
                case 'p':
                    this.image = R.drawable.ic_phone_black_24dp;
                    break;
                case 'e':
                    this.image = R.drawable.ic_email_black_24dp;
                    break;
                case 's':
                    Log.i("CARDDEBUG","Type: " + this.social.getType());
                    switch (this.social.getType()){
                        case "Google+":
                            this.image = R.drawable.google_plus_24dp;
                            break;
                        case "Twitter":
                            this.image = R.drawable.icons8_twitter;
                            break;
                        case "LinkedIn":
                            this.image = R.drawable.icons8_linkedin;
                            break;
                        case "Spotify":
                            this.image= R.drawable.ic_spotify_24dp;
                            break;
                        case "Facebook":
                            this.image = R.drawable.fb_24dp;
                            break;
                    }
                    break;
            }
    }

    public int getImage() {return image;}

    public char getTag() {
        return tag;
    }


    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Social getSocial() {
        return social;
    }

}
