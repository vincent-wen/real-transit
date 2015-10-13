package com.vincent.realtransit.helper;

/**
 * Created by vincent on 2015-10-12.
 */
public class SQLHelper {
    private static SQLHelper instance;

    private SQLHelper () {
    }

    public static SQLHelper getInstance () {
        if (instance == null)
            instance = new SQLHelper();
        return instance;
    }


}
