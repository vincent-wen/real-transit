package com.vincent.realtransit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.vincent.realtransit.DataHolder;
import com.vincent.realtransit.R;
import com.vincent.realtransit.helper.Constants;
import com.vincent.realtransit.helper.Manager;
import com.vincent.realtransit.service.ServiceUpdateStopSchedule;

public class StopDetailActivity extends AppCompatActivity {
    private final StopDetailActivity activity = this;
    private BroadcastReceiver receiver;
    private ExpandableListView listView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("StopDetailActivity", "Creating interface");
        setContentView(R.layout.activity_stop_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ExpandableListView) findViewById(R.id.expandableListView);
        textView = (TextView) findViewById(R.id.textView);

        Log.v("StopDetailActivity", "Register receiver");
        receiver = new UpdateStopScheduleReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver,
                new IntentFilter(ServiceUpdateStopSchedule.ACTION));

        Log.v("StopDetailActivity", "Create intent");
        Intent intent = new Intent(this, ServiceUpdateStopSchedule.class);
        intent.putExtra(Constants.STOP_NUM, getIntent().getIntExtra(Constants.STOP_NUM, -1));
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        Log.v("StopDetailActivity", "onDestroy");
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    private class UpdateStopScheduleReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("StopDetailActivity", "Received an intent");
            String error = intent.getStringExtra("error");
            if (error != null) {
                textView.setText(error);
                ((ViewManager) listView.getParent()).removeView(listView);
            }
            else {
                Log.v("UpdateStopSchedule", "creating an adapter");
                SimpleExpandableListAdapter adapter = createAdapter(activity);
                listView.setAdapter(adapter);
                ((ViewManager) textView.getParent()).removeView(textView);
                listView.setVisibility(View.VISIBLE);
            }
            Manager.releaseResources();
        }
    }

    private SimpleExpandableListAdapter createAdapter(Context context) {
        return new SimpleExpandableListAdapter(
                context,
                DataHolder.busNums,
                android.R.layout.simple_expandable_list_item_1,
                new String[] {Constants.TEXT},
                new int[] {android.R.id.text1},
                DataHolder.busSchedules,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {Constants.TEXT},
                new int[] {android.R.id.text1}
                );
    }
}
