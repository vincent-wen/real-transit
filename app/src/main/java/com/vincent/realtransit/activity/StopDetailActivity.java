package com.vincent.realtransit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.vincent.realtransit.R;
import com.vincent.realtransit.helper.Constants;
import com.vincent.realtransit.helper.Manager;
import com.vincent.realtransit.service.UpdateStopScheduleService;

public class StopDetailActivity extends AppCompatActivity {
    private final StopDetailActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new UpdateStopScheduleReceiver(),
                new IntentFilter(UpdateStopScheduleService.ACTION));

        setContentView(R.layout.activity_stop_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private class UpdateStopScheduleReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getSerializableExtra(Constants.STOP_SCHEDULE_FLAG) != Constants.STATUS.OK) {
                return;
            }
            ExpandableListView detailView = (ExpandableListView) activity.findViewById(R.id.expandableListView);
            if (detailView.getAdapter() == null) {
                SimpleExpandableListAdapter detailAdapter = createAdapter(activity);
                detailView.setAdapter(detailAdapter);
            }
            Manager.releaseResources();
        }
    }

    private SimpleExpandableListAdapter createAdapter(Context context) {
        return new SimpleExpandableListAdapter(
                context,
                UpdateStopScheduleService.busNums,
                android.R.layout.simple_expandable_list_item_1,
                new String[] {UpdateStopScheduleService.STRING},
                new int[] {android.R.id.text1},
                UpdateStopScheduleService.busSchedules,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {UpdateStopScheduleService.STRING},
                new int[] {android.R.id.text1}
                );
    }

}
