package com.vincent.realtransit.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.vincent.realtransit.DataHolder;
import com.vincent.realtransit.R;
import com.vincent.realtransit.helper.Constants;
import com.vincent.realtransit.helper.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceStopSchedule extends IntentService {
    public static final String ACTION = ServiceStopSchedule.class.getName();
    private String error;

    public ServiceStopSchedule() {
        super("ServiceStopSchedule");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.v("UpdateStopSchedule", "service new intent");
            int stopNum = intent.getIntExtra(Constants.STOP_NUM, -1);
            if (stopNum < 0) return;

            String response = HttpHelper.getInstance().stopSchedule(stopNum, getString(R.string.api_key));
            grabSchedules(response);
            intent = new Intent(ACTION);
            if (error != null) {
                intent.putExtra("error", error);
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } catch (Exception e) {
            Log.v("UpdateStopSchedule", "onHandleIntent", e);
        }
    }

    private void grabSchedules (String response) throws JSONException {
        DataHolder.busNums.clear();
        DataHolder.busSchedules.clear();

        JSONArray buses = null;
        try {
            buses = new JSONArray(response);
        } catch (JSONException e) {
            JSONObject err = new JSONObject(response);
            if (err.has("Code")) {
                error = err.getString("Message");
                return;
            }
        }

        for (int i = 0; i < buses.length(); i++) {
            JSONObject bus = buses.getJSONObject(i);
            Map<String, String> busNumMap = new HashMap<>(1);
            busNumMap.put(Constants.TEXT, bus.getString("RouteNo"));

            List<Map<String, String>> schedules = new ArrayList<>();
            JSONArray schedulesJson = bus.getJSONArray("Schedules");

            for (int j = 0; j < schedulesJson.length(); j++) {
                JSONObject scheduleJson = schedulesJson.getJSONObject(j);
                Map<String, String> schedule = new HashMap<>(1);
                schedule.put(Constants.TEXT, scheduleJson.getString("ExpectedLeaveTime").split(" ")[0]);
                schedules.add(schedule);
            }
            DataHolder.busSchedules.add(schedules);
            DataHolder.busNums.add(busNumMap);
        }
    }
}
