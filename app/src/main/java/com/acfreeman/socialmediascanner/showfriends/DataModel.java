package com.acfreeman.socialmediascanner.showfriends;

import android.graphics.Bitmap;

import com.acfreeman.socialmediascanner.db.Email;
import com.acfreeman.socialmediascanner.db.Phone;
import com.acfreeman.socialmediascanner.db.Social;

import java.util.ArrayList;

/**
 * Created by Andrew on 10/13/2017.
 */

public class DataModel {

    String name;
    long id;
    byte[] image;
    String type;
    String version_number;
    String feature;
    ArrayList<Phone> phones;
    ArrayList<Email> emails;
    ArrayList<Social> socials;
    Bitmap bitmap;

    public DataModel(String name, long id, ArrayList<Phone> phones, ArrayList<Email> emails, ArrayList<Social> socials, byte[] image) {
        this.name=name;
        this.id=id;
        this.phones=phones;
        this.emails=emails;
        this.socials=socials;
        this.image = image;
//        this.bitmap=bitmap;
    }

    public String getName() {
        return name;
    }

    public long getId() {return id;}

    public ArrayList<Phone> getPhones() {
        return phones;
    }

    public ArrayList<Email> getEmails() {
        return emails;
    }

    public ArrayList<Social> getSocials() {
        return socials;
    }

    public byte[] getImage() { return image;}
    public Bitmap getBitmap() {return bitmap;}

}
