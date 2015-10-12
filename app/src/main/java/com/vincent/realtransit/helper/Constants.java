package com.vincent.realtransit.helper;

/**
 * Created by vincent on 2015-10-10.
 */
public class Constants {
    public static final String PACKAGE_NAME = Constants.class.getName().substring(0, Constants.class.getName().lastIndexOf('.'));
    public static final String STOP_NUM_FLAG = PACKAGE_NAME + ".flag.StopNUM";
    public static final String STOP_SCHEDULE_FLAG = PACKAGE_NAME + ".flag.StopSchedule";

    public enum STATUS {
        OK, NOK, RUNNING
    }
}
