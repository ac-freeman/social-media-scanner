package com.acfreeman.socialmediascanner.showfriends;

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
    String type;
    String version_number;
    String feature;
    ArrayList<Phone> phones;
    ArrayList<Email> emails;
    ArrayList<Social> socials;

    public DataModel(String name, long id, ArrayList<Phone> phones, ArrayList<Email> emails, ArrayList<Social> socials) {
        this.name=name;
        this.id=id;
        this.phones=phones;
        this.emails=emails;
        this.socials=socials;

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

}
