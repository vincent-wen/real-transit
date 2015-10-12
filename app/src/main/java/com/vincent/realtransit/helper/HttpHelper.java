package com.vincent.realtransit.helper;

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

    public String stopSchedule(int stopNum, String apiKey) throws Exception {
        URL url = new URL("http://api.translink.ca/RTTIAPI/V1/stops/" + stopNum + "/estimates?apikey=" + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/JSON");

        String result = readResponse(connection.getInputStream());
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
        return result;
    }
}
