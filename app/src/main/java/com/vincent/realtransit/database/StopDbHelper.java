package com.vincent.realtransit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vincent on 2015-10-12.
 */
public class StopDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Stop.db";
    private static String TEXT_TYPE = " TEXT";
    private static String COMMA_SEP = ",";
    private static String SQL_CREATE_STOP_TABLE =
            "CREATE TABLE " + DbContract.Stop.TABLE_NAME + " (" +
                    DbContract.Stop._ID + " INTEGER PRIMARY KEY," +
                    DbContract.Stop.COLUMN_NAME_NO + TEXT_TYPE + COMMA_SEP +
                    DbContract.Stop.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    DbContract.Stop.COLUMN_NAME_ROUTES + TEXT_TYPE +
                    " )";
    private static String SQL_DELETE_STOP_TABLE =
            "DROP TABLE IF EXISTS " + DbContract.Stop.TABLE_NAME;

    public StopDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STOP_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_STOP_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
