package com.vincent.realtransit.helper;

import com.vincent.realtransit.service.UpdateStopScheduleService;

/**
 * Created by vincent on 2015-10-11.
 */
public class Manager {
    public static void releaseResources () {
        HttpHelper.destroy();
    }
}
