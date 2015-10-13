package com.vincent.realtransit.database;

import android.provider.BaseColumns;

/**
 * Created by vincent on 2015-10-12.
 */
public final class DbContract {
    private DbContract() {}

    /* Inner class that defines the table contents */
    public abstract class Stop implements BaseColumns {
        public static final String TABLE_NAME = "stop";
        public static final String COLUMN_NAME_NO = "no";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ROUTES = "routes";
    }

}
