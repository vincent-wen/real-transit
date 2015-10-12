package com.vincent.realtransit.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

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

public class UpdateStopScheduleService extends IntentService {
    public static String ACTION;
    public static List<Map<String, String>> busNums = new ArrayList<>();
    public static List<List<Map<String, String>>> busSchedules = new ArrayList<>();
    public static final String STRING = "STRING";

    public UpdateStopScheduleService() {
        super("Update Stop Info");
        ACTION = this.getClass().getName();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            int stopNum = intent.getIntExtra(Constants.STOP_NUM_FLAG, 0);
            if (stopNum <= 0) return;

            String allSchedules = HttpHelper.getInstance().stopSchedule(stopNum, getString(R.string.api_key));
            grabSchedules(allSchedules);
            intent = new Intent(ACTION);
            intent.putExtra(Constants.STOP_SCHEDULE_FLAG, Constants.STATUS.OK);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void grabSchedules (String allSchedules) throws JSONException {
        busNums.clear();
        busSchedules.clear();

        JSONArray buses = new JSONArray(allSchedules);
        for(int i=0; i<buses.length(); i++) {
            JSONObject bus = buses.getJSONObject(i);
            Map<String, String> busNumMap = new HashMap<>(1);
            busNumMap.put(STRING, bus.getString("RouteNo"));

            List<Map<String, String>> schedules = new ArrayList<>();
            JSONArray schedulesJson = bus.getJSONArray("Schedules");

            for (int j=0; j<schedulesJson.length(); j++) {
                JSONObject scheduleJson = schedulesJson.getJSONObject(j);
                Map<String, String> schedule = new HashMap<>(1);
                schedule.put(STRING, scheduleJson.getString("ExpectedLeaveTime"));
                schedules.add(schedule);
            }
            busSchedules.add(schedules);
            busNums.add(busNumMap);
        }
    }
}
