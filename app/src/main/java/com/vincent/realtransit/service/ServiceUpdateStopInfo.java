package com.vincent.realtransit.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.vincent.realtransit.DataHolder;
import com.vincent.realtransit.R;
import com.vincent.realtransit.database.DbContract;
import com.vincent.realtransit.database.StopDbHelper;
import com.vincent.realtransit.helper.Constants;
import com.vincent.realtransit.helper.HttpHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vincent on 2015-10-12.
 */
public class ServiceUpdateStopInfo extends IntentService {
    public static final String ACTION = ServiceUpdateStopInfo.class.getName();
    private String error;

    public ServiceUpdateStopInfo() {
        super("ServiceUpdateStopInfo");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.v("ServiceUpdateStopInfo", "Service new intent");
            String stopNo = intent.getStringExtra(Constants.CREATE_STOP);
            intent = new Intent(ACTION);
            if (stopNo != null && stopNo.length() > 0) {
                Log.v("StopNo intent", stopNo);
                String response = HttpHelper.getInstance().stopInfo(stopNo, getString(R.string.api_key));
                Map<String, String> stop = grabStopInfo(response);
                if (stop != null) {
                    dbUpdateStop(stop);
                } else {
                    intent.putExtra("error", error);
                }
            } else {
                dbGetStops();
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } catch (Exception e) {
            Log.v("ServiceUpdateStopInfo", "onHandleIntent", e);
        }
    }

    private Map<String, String> grabStopInfo(String response) throws JSONException {
        Map<String, String> stop = new HashMap<>(2);
        JSONObject stopJson = new JSONObject(response);
        if (stopJson.has("Code")) {
            error = stopJson.getString("Message");
            return null;
        }
        stop.put(DbContract.Stop.COLUMN_NAME_NO, stopJson.getString("StopNo"));
        stop.put(DbContract.Stop.COLUMN_NAME_NAME, stopJson.getString("Name"));
        stop.put(DbContract.Stop.COLUMN_NAME_ROUTES, stopJson.getString("Routes"));

        DataHolder.stops.add(stop);
        return stop;
    }

    private void dbUpdateStop(Map<String, String> stop) {
        StopDbHelper stopDbHelper = new StopDbHelper(getBaseContext());
        SQLiteDatabase stopDb = stopDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Stop.COLUMN_NAME_NAME, stop.get(DbContract.Stop.COLUMN_NAME_NAME));
        values.put(DbContract.Stop.COLUMN_NAME_NO, stop.get(DbContract.Stop.COLUMN_NAME_NO));
        values.put(DbContract.Stop.COLUMN_NAME_ROUTES, stop.get(DbContract.Stop.COLUMN_NAME_ROUTES));

        stopDb.insert(DbContract.Stop.TABLE_NAME, null, values);
        Log.v("dbUpdateStop", "Successfully inserted");
    }

    private void dbGetStops () {
        StopDbHelper stopDbHelper = new StopDbHelper(getBaseContext());
        SQLiteDatabase stopDb = stopDbHelper.getReadableDatabase();
        String[] projection = {
                DbContract.Stop.COLUMN_NAME_NO,
                DbContract.Stop.COLUMN_NAME_NAME,
                DbContract.Stop.COLUMN_NAME_ROUTES
        };
        Cursor c = stopDb.query(
                DbContract.Stop.TABLE_NAME,
                projection,
                null,null,null,null,null
        );
        DataHolder.stops.clear();
        while (c.moveToNext()) {
            Map<String, String> stop = new HashMap<>();
            stop.put(DbContract.Stop.COLUMN_NAME_NO,
                    c.getString(c.getColumnIndex(DbContract.Stop.COLUMN_NAME_NO))
                    );
            stop.put(DbContract.Stop.COLUMN_NAME_NAME,
                    c.getString(c.getColumnIndex(DbContract.Stop.COLUMN_NAME_NAME))
            );
            stop.put(DbContract.Stop.COLUMN_NAME_ROUTES,
                    c.getString(c.getColumnIndex(DbContract.Stop.COLUMN_NAME_ROUTES))
            );
            DataHolder.stops.add(stop);
        }
    }
}
