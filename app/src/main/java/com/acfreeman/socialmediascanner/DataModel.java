package com.acfreeman.socialmediascanner;

import com.acfreeman.socialmediascanner.db.Emails;
import com.acfreeman.socialmediascanner.db.Phones;
import com.acfreeman.socialmediascanner.db.Social;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 10/13/2017.
 */

public class DataModel {

    String name;
    long id;
    String type;
    String version_number;
    String feature;
    ArrayList<Phones> phones;
    ArrayList<Emails> emails;
    ArrayList<Social> socials;

    public DataModel(String name, long id,  ArrayList<Phones> phones, ArrayList<Emails> emails, ArrayList<Social> socials) {
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

    public ArrayList<Phones> getPhones() {
        return phones;
    }

    public ArrayList<Emails> getEmails() {
        return emails;
    }

    public ArrayList<Social> getSocials() {
        return socials;
    }

}
