package com.vincent.realtransit.helper;

/**
 * Created by vincent on 2015-10-10.
 */
public class Constants {
    public static final String PACKAGE_NAME = Constants.class.getName().substring(0, Constants.class.getName().lastIndexOf('.'));

    public static final String TEXT = "TEXT";
    public static final String STOP_NUM = PACKAGE_NAME + ".STOP_NUM";
    public static final String UPDATE_RESULT = PACKAGE_NAME + ".UPDATE_RESULT";
    public static final String CREATE_STOP = PACKAGE_NAME + ".CREATE_STOP";
    public static final String DELETE_STOP = PACKAGE_NAME + ".DELETE_STOP";

    public enum STATUS {
        OK, NOK
    }
}
