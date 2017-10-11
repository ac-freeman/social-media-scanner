package com.acfreeman.socialmediascanner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzj_0 on 2017/9/19.
 */

public class LocalDatabase extends SQLiteOpenHelper{
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "userInfo";
    // Contacts table name
    private static final String TABLE_EMAILS = "Emails";
    private static final String TABLE_OWNER = "Owner";
    private static final String TABLE_PHONES = "Phones";
    private static final String TABLE_SOCIAL = "Social";
    private static final String TABLE_CONTACTS = "Contacts";
    //private static final String TABLE_USERS = "users";
    // Users Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EM_ADDR = "user_email_address";
    private static final String KEY_EM_TYPE = "email_type";
    private static final String KEY_PH_NUMBER = "phone_number";
    private static final String KEY_PH_TYPE = "phone_number_type";
    private static final String KEY_SO_TYPE = "social_type";
    private static final String KEY_USERNAME = "username";

    public LocalDatabase(Context context) {
       super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
 //       String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
//               + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
 //               + KEY_EM_ADDR + " TEXT" + ")";
//        db.execSQL(CREATE_CONTACTS_TABLE);



        String CREATE_OWNER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_OWNER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";
        db.execSQL(CREATE_OWNER_TABLE);

        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + "("
                 + KEY_NAME + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_PHONES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PHONES + "("
                + KEY_ID + " INTEGER," + KEY_PH_NUMBER + " TEXT,"
                + KEY_PH_TYPE + " TEXT" + ")";
        db.execSQL(CREATE_PHONES_TABLE);

        String CREATE_EMAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_EMAILS + "("
                + KEY_ID + " INTEGER," + KEY_EM_ADDR + " TEXT,"
                + KEY_EM_TYPE + " TEXT" + ")";
        db.execSQL(CREATE_EMAILS_TABLE);


        String CREATE_SOCIAL_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SOCIAL + "("
                + KEY_ID + " INTEGER," + KEY_SO_TYPE + " TEXT,"
                + KEY_USERNAME + " TEXT" + ")";
        db.execSQL(CREATE_SOCIAL_TABLE);


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP OWNER IF EXISTS " + TABLE_OWNER);
        db.execSQL("DROP PHONES IF EXISTS " + TABLE_PHONES);
        db.execSQL("DROP EMAILS IF EXISTS " + TABLE_EMAILS);
        db.execSQL("DROP CONTACTS IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP SOCIAL IF EXISTS " + TABLE_SOCIAL);
// Creating tables again
        onCreate(db);
    }
    // Adding new user
//    public void addUser(User user) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
 //       ContentValues values = new ContentValues();
 //       values.put(KEY_NAME, user.getName()); // User Name
 //       values.put(KEY_EM_ADDR, user.getAddress()); // User Phone Number

// Inserting Row
 //       db.insert(TABLE_USERS, null, values);
 //       db.close(); // Closing database connection
 //   }


    //***********************************************************************************************
    // Adding new user
    public void addOwner(Owner owner) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, owner.getId()); // Owner ID
        values.put(KEY_NAME, owner.getName()); // Owner Name

// Inserting Row
        db.insert(TABLE_OWNER, null, values);
        db.close(); // Closing database connection
    }

    public void addPhones(Phones phones) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, phones.getId()); // id
        values.put(KEY_PH_NUMBER, phones.getNumber()); // phone number
        values.put(KEY_PH_TYPE, phones.getType()); // phone number type
// Inserting Row
        db.insert(TABLE_PHONES, null, values);
        db.close(); // Closing database connection
    }

    public void addEmails(Emails emails) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, emails.getId()); // id
        values.put(KEY_EM_ADDR, emails.getEmail()); // email
        values.put(KEY_EM_TYPE, emails.getType()); // email type
// Inserting Row
        db.insert(TABLE_EMAILS, null, values);
        db.close(); // Closing database connection
    }

    public void addContacts(Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(KEY_ID, contacts.getId()); // Owner ID
        values.put(KEY_NAME, contacts.getName()); // Owner Name

// Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        long lastId;
        String selectQuery = "SELECT ROWID from "+ TABLE_CONTACTS + " order by ROWID DESC limit 1";




        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {
            lastId = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
            contacts.setId(lastId);
        }



        db.close(); // Closing database connection

    }

    ///////////
    public void getContactId(Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();

    }

    public void addSocial(Social social) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, social.getId()); // id
        values.put(KEY_SO_TYPE, social.getType()); // social type
        values.put(KEY_USERNAME, social.getUsername()); // username
// Inserting Row
        db.insert(TABLE_SOCIAL, null, values);
        db.close(); // Closing database connection
    }
    //***************************************************************************************


    //**************************************************************************************
    // Getting one user
    public Owner getOwner(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OWNER, new String[]{KEY_ID,
                KEY_NAME}, KEY_ID + "=?",
        new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Owner ownerA = new Owner(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));
// return user
        return ownerA;
    }

    public Phones getPhones(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PHONES, new String[]{KEY_ID,
                        KEY_PH_NUMBER, KEY_PH_TYPE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Phones phoneA = new Phones(Long.parseLong(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)), cursor.getString(2));
// return user
        return phoneA;
    }

    public Emails getEmail(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EMAILS, new String[]{KEY_ID,
                        KEY_EM_ADDR, KEY_EM_TYPE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Emails emailsA = new Emails(Long.parseLong(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
// return user
        return emailsA;
    }

    public Contacts getContacts(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID,
                        KEY_NAME}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Contacts contactsA = new Contacts(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));
// return user
        return contactsA;
    }

    public Social getSocial(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SOCIAL, new String[]{KEY_ID,
                        KEY_SO_TYPE, KEY_USERNAME}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Social socialA = new Social(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
// return user
        return socialA;
    }


    // Getting All Users
    public List<Phones> getAllPhones() {
        List<Phones> phonesList = new ArrayList<Phones>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PHONES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Phones phones = new Phones();
                phones.setId(Integer.parseInt(cursor.getString(0)));
                phones.setNumber(Integer.parseInt(cursor.getString(1)));
                phones.setType(cursor.getString(2));
// Adding contact to list
                phonesList.add(phones);
            } while (cursor.moveToNext());
        }

// return contact list
        return phonesList;
    }

    public List<Owner> getAllOwner() {
        List<Owner> ownerList = new ArrayList<Owner>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_OWNER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Owner owner = new Owner();
                owner.setId(Integer.parseInt(cursor.getString(0)));
                owner.setName(cursor.getString(1));
// Adding contact to list
                ownerList.add(owner);
            } while (cursor.moveToNext());
        }

// return contact list
        return ownerList;
    }

    public List<Emails> getAllEmails() {
        List<Emails> emailsList = new ArrayList<Emails>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_EMAILS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Emails emails = new Emails();
                emails.setId(Integer.parseInt(cursor.getString(0)));
                emails.setEmail(cursor.getString(1));
                emails.setType(cursor.getString(2));
// Adding contact to list
                emailsList.add(emails);
            } while (cursor.moveToNext());
        }

// return contact list
        return emailsList;
    }

    public List<Contacts> getAllContacts() {
        List<Contacts> contactsList = new ArrayList<Contacts>();
// Select All Query
        String selectQuery = "SELECT rowid, * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contacts contacts = new Contacts();
                contacts.setId(Long.parseLong(cursor.getString(0)));
                contacts.setName(cursor.getString(1));
// Adding contact to list
                contactsList.add(contacts);
            } while (cursor.moveToNext());
        }

// return contact list
        return contactsList;
    }

    public List<Social> getAllSocial() {
        List<Social> socialList = new ArrayList<Social>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_SOCIAL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Social social = new Social();
                social.setId(Integer.parseInt(cursor.getString(0)));
                social.setType(cursor.getString(1));
                social.setUsername(cursor.getString(2));
// Adding contact to list
                socialList.add(social);
            } while (cursor.moveToNext());
        }

// return contact list
        return socialList;
    }
//******************************************************************


    public ArrayList<Emails> getUserEmails(long id) {

        String countQuery = "SELECT * FROM " + TABLE_EMAILS +" WHERE " + KEY_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        Log.i("DATABASEDEBUG",String.valueOf(cursor.getCount()));

        ArrayList<Emails> emails = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Emails email = new Emails(Long.parseLong(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2));
                emails.add(email);
            } while (cursor.moveToNext());
        }
        return emails;
    }

    public ArrayList<Phones> getUserPhones(long id) {

        String countQuery = "SELECT * FROM " + TABLE_PHONES +" WHERE " + KEY_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        Log.i("DATABASEDEBUG",String.valueOf(cursor.getCount()));

        ArrayList<Phones> phones = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Phones phone = new Phones(Long.parseLong(cursor.getString(0)),
                        cursor.getInt(1), cursor.getString(2));
                phones.add(phone);
            } while (cursor.moveToNext());
        }
        return phones;
    }

    public ArrayList<Social> getUserSocials(long id) {

        String countQuery = "SELECT * FROM " + TABLE_SOCIAL +" WHERE " + KEY_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        Log.d("DATABASEDEBUG",String.valueOf(cursor.getCount()));

        ArrayList<Social> socials = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Social social = new Social(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),cursor.getString(2));
                socials.add(social);
            } while (cursor.moveToNext());
        }
        return socials;
    }




//******************************************************************
    // Getting users Count
    public int getPhonesCount() {
        String countQuery = "SELECT * FROM " + TABLE_PHONES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

// return count
        return cursor.getCount();
    }

    public int getOwnerCount() {
        String countQuery = "SELECT * FROM " + TABLE_OWNER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

// return count
        return cursor.getCount();
    }

    public int getEmailsCount() {
        String countQuery = "SELECT * FROM " + TABLE_EMAILS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

// return count
        return cursor.getCount();
    }

    public int getContactsCount() {
        String countQuery = "SELECT * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

// return count
        return cursor.getCount();
    }

    public int getSocialCount() {
        String countQuery = "SELECT * FROM " + TABLE_SOCIAL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

// return count
        return cursor.getCount();
    }


    //***************************************************************************************
    // Updating a user
    public int updatePhones(Phones phones) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PH_NUMBER, phones.getNumber());
        values.put(KEY_PH_TYPE, phones.getType());

// updating row
        return db.update(TABLE_PHONES, values, KEY_ID + " = ?",
        new String[]{String.valueOf(phones.getId())});
    }

    public int updateOwner(Owner owner) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, owner.getName());

// updating row
        return db.update(TABLE_OWNER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(owner.getId())});
    }

    public int updateEmails(Emails emails) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EM_ADDR, emails.getEmail());
        values.put(KEY_EM_TYPE, emails.getType());

// updating row
        return db.update(TABLE_EMAILS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(emails.getId())});
    }

    public int updateContacts(Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contacts.getName());

// updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(contacts.getId())});
    }

    public int updateSocial(Social social) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SO_TYPE, social.getType());
        values.put(KEY_USERNAME, social.getUsername());

// updating row
        return db.update(TABLE_SOCIAL, values, KEY_ID + " = ?",
                new String[]{String.valueOf(social.getId())});
    }

//********************************************************************************************
    // Deleting a user
    public void deletePhones(Phones phones) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHONES, KEY_ID + " = ?",
        new String[] { String.valueOf(phones.getId()) });
        db.close();
    }

    public void deleteOwner(Owner owner) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OWNER, KEY_ID + " = ?",
                new String[] { String.valueOf(owner.getId()) });
        db.close();
    }

    public void deleteEmails(Emails emails) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EMAILS, KEY_ID + " = ?",
                new String[] { String.valueOf(emails.getId()) });
        db.close();
    }

    public void deleteContacts(Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contacts.getId()) });
        db.close();
    }
    //******************************************************************************************
}
