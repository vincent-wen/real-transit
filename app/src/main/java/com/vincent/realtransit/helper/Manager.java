package com.vincent.realtransit.helper;

import android.util.Log;

/**
 * Created by vincent on 2015-10-11.
 */
public class Manager {
    public static void releaseResources () {
        Log.v("Manager", "Releasing resources");
        HttpHelper.destroy();
    }
}
