package com.acfreeman.socialmediascanner;

import android.provider.BaseColumns;

/**
 * Created by Andrew on 9/14/2017.
 */

public final class DBContract{
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBContract() {}

    /* Inner class that defines the table contents */
    public static class DBEntry implements BaseColumns {
        public static final String TABLE_NAME = "owner";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String PHONE = "phone";
    }


}
