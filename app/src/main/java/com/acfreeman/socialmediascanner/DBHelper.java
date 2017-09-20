package com.acfreeman.socialmediascanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Andrew on 9/14/2017.
 */


public class DBHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_OWNER =
            "CREATE TABLE " + DBContract.DBOwner.TABLE_NAME + " (" +
                    DBContract.DBOwner._ID + " INTEGER PRIMARY KEY," +
                    DBContract.DBOwner.NAME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.DBOwner.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Contacts.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_OWNER);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}