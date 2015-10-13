package com.vincent.realtransit.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleAdapter;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.vincent.realtransit.DataHolder;
import com.vincent.realtransit.R;
import com.vincent.realtransit.database.DbContract;
import com.vincent.realtransit.helper.Constants;
import com.vincent.realtransit.helper.Manager;
import com.vincent.realtransit.service.ServiceStopInfo;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final MainActivity context = this;
    private AlertDialog newStopdialog;
    private AlertDialog delStopDialog;
    private EditText userInput;
    private BroadcastReceiver receiver;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init interface
        setContentView(R.layout.activity_main);

        // init receiver
        initReceiver();

        // create intent to undate stop info
        intentUpdateStopInfo(context);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (newStopdialog == null) {
                    userInput = new EditText(context);
                    newStopdialog = new AlertDialog.Builder(context)
                        .setPositiveButton("Save", null)
                        .setNegativeButton("Cancel", null)
                        .create();
                    newStopdialog.setTitle("Add New Stop");
                    newStopdialog.setMessage("Please input a new stop number");
                    newStopdialog.setView(userInput, 60, 0, 60, 0);
                    newStopdialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(final DialogInterface di) {
                            newStopdialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String stopNo = userInput.getText().toString().trim();
                                            int newStopNo;
                                            try {
                                                Integer.valueOf(stopNo);
                                                if (isExist(stopNo)) {
                                                    Snackbar.make(v, "The bus stop already exists.", Snackbar.LENGTH_SHORT).show();
                                                } else {
                                                    di.dismiss();
                                                    intentSaveNewStop(stopNo);
                                                }
                                            } catch (NumberFormatException e) {
                                                Log.v("MainActivity", "invalid number");
                                                Snackbar.make(view, "Please input a valid number.", Snackbar.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }
                userInput.setText("");
                newStopdialog.show();
            }
        });

        // create adapter
        adapter = createAdapter(this, DataHolder.stops);
        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, StopDetailActivity.class);
                int stopNum = Integer.valueOf(DataHolder.stops.get(position).get(DbContract.Stop.COLUMN_NAME_NO));
                Log.v("MainActivity", "Stop Number selected: " + stopNum);
                intent.putExtra(Constants.STOP_NUM, stopNum);
                context.startActivity(intent);
            }
        });

        // swap item to delete
        listView.setMenuCreator(new ItemSwipeMenuCreator());
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    if (delStopDialog == null) {
                        delStopDialog = new AlertDialog.Builder(context)
                                .setTitle("Confirm")
                                .setMessage("Are you sure to remove the bus stop?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    private int pos = position;
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        intentDeleteStop(pos);
                                    }
                                })
                                .setNegativeButton("No", null)
                                .create();
                    }
                    delStopDialog.show();
                }
                return false;
            }
        });
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
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

    @Override
    protected void onDestroy() {
        Log.v("MainActivity", "onDestroy");
        if (receiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    private SimpleAdapter createAdapter (Context context, List<Map<String, String>> stops) {
        return new SimpleAdapter(
                context,
                stops,
                android.R.layout.simple_list_item_2,
                new String[] {
                        DbContract.Stop.COLUMN_NAME_NO,
                        DbContract.Stop.COLUMN_NAME_NAME
                },
                new int[] {android.R.id.text1, android.R.id.text2}
                );
    }

    private boolean isExist (String stopNo) {
        for (Map<String, String> stop : DataHolder.stops) {
            if (stop.get(DbContract.Stop.COLUMN_NAME_NO).equals(stopNo))
                return true;
        }
        return false;
    }

    private void intentUpdateStopInfo (Context context) {
        Log.v("MainActivity", "intent to updat stop info");
        Intent intent = new Intent(context, ServiceStopInfo.class);
        context.startService(intent);
    }

    private void intentSaveNewStop(String stopNo) {
        Log.v("MainActivity", "intent to creat stop");
        Intent intent = new Intent(context, ServiceStopInfo.class);
        intent.putExtra(Constants.CREATE_STOP, stopNo);
        context.startService(intent);
    }

    private void intentDeleteStop(int position) {
        Log.v("MainActivity", "intent to delete stop " + position);
        String stopNo = DataHolder.stops.get(position).get(DbContract.Stop.COLUMN_NAME_NO);
        Intent intent = new Intent(context, ServiceStopInfo.class);
        intent.putExtra(Constants.DELETE_STOP, stopNo);
        context.startService(intent);
    }

    private void initReceiver () {
        receiver = new UpdateStopInfoReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(
                receiver,
                new IntentFilter(ServiceStopInfo.ACTION)
        );
        Log.v("MainActivity", "Receiver initied");
    }

    private class UpdateStopInfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent) {
            Log.v("MainActivity", "Received an intent");
            String error = intent.getStringExtra("error");
            if (error != null) {
                Log.v("MainActivity", "Received an error");
                Snackbar.make(context.findViewById(android.R.id.content), error, Snackbar.LENGTH_SHORT).show();
            } else {
                adapter.notifyDataSetChanged();
            }
            Manager.releaseResources();
        }
    }

    private class ItemSwipeMenuCreator implements SwipeMenuCreator {
        @Override
        public void create(SwipeMenu menu) {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.parseColor("#EF5350")));
            // set item width
            deleteItem.setWidth(250);
            // set a icon
            deleteItem.setIcon(android.R.drawable.ic_menu_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    }
}
