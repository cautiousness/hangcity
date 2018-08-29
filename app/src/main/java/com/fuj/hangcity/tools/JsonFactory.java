package com.fuj.hangcity.tools;

import android.content.Context;

import com.fuj.hangcity.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by fuj
 */
public class JsonFactory {
    private static Properties properties;

    static {
        properties = new Properties();
    }

    public static String getJsonString(int page, Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.json);
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            properties.load(reader);
            return properties.getProperty(String.valueOf(page));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}