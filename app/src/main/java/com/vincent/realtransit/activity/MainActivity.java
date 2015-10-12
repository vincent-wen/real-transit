package com.vincent.realtransit.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.vincent.realtransit.R;
import com.vincent.realtransit.helper.Constants;
import com.vincent.realtransit.service.UpdateStopScheduleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final List<Map<String, String>> stops = prepareData();
        SimpleAdapter adapter = prepareAdapter(this, stops);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // item click
        final MainActivity context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, UpdateStopScheduleService.class);
                int stopNum = Integer.valueOf(stops.get(position).get("Num"));
                intent.putExtra(Constants.STOP_NUM_FLAG, stopNum);
                context.startService(intent);

                intent = new Intent(context, StopDetailActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Map<String, String>> prepareData () {
        List<Map<String, String>> stops = new ArrayList<>();
        Map<String, String> stop = new HashMap<>(2);
        stop.put("Num", "58342");
        stop.put("Name", "NB Holdom Ave NS Lougheed Hwy");
        stops.add(stop);

        stop = new HashMap<>(2);
        stop.put("Num", "52910");
        stop.put("Name", "SB Holdom Ave FS Parker St");
        stops.add(stop);

        stop = new HashMap<>(2);
        stop.put("Num", "52940");
        stop.put("Name", "NB Holdom Ave FS Curtis St");
        stops.add(stop);

        stop = new HashMap<>(2);
        stop.put("Num", "51874");
        stop.put("Name", "WB Hastings st FS Holdom Ave");
        stops.add(stop);

        stop = new HashMap<>(2);
        stop.put("Num", "52515");
        stop.put("Name", "PATTERSON STN BAY 1");
        stops.add(stop);
        return stops;
    }

    private SimpleAdapter prepareAdapter (Context context, List<Map<String, String>> stops) {
        return new SimpleAdapter(
                context,
                stops,
                android.R.layout.simple_list_item_2,
                new String[] {"Num", "Name"},
                new int[] {android.R.id.text1, android.R.id.text2}
                );
    }
}
