package com.vincent.realtransit.helper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vincent on 2015-10-10.
 */
public class HttpHelper {
    private static HttpHelper instance = null;

    private HttpHelper() {
    }

    public static HttpHelper getInstance () {
        if (instance == null) {
            instance = new HttpHelper();
        }
        return instance;
    }

    public static void destroy () {
        instance = null;
    }

    public String stopSchedule(int stopNo, String apiKey) throws Exception {
        return get("http://api.translink.ca/RTTIAPI/V1/stops/" + stopNo + "/estimates?apikey=" + apiKey);
    }

    public String stopInfo(String stopNo, String apiKey) throws Exception {
        return get("http://api.translink.ca/RTTIAPI/V1/stops/" + stopNo + "?apikey=" + apiKey);
    }

    private String get (String rawUrl) throws Exception{
        URL url = new URL(rawUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/JSON");

        int status = connection.getResponseCode();
        InputStream in = status <= 300 ? connection.getInputStream() : connection.getErrorStream();

        String result = readResponse(in);
        connection.disconnect();
        return result;
    }

    private String readResponse (InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        String result = sb.toString();
        Log.v("HttpResponse", result);
        return result;
    }
}
