package com.fuj.hangcity.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * @author fuj
 */
public class PreferenceUtils {
	public static void write(Context context, String SharedPreferencesName, String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(key, value).apply();
	}
	
	public static void write(Context context, String SharedPreferencesName, String key, int value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
		sharedPreferences.edit().putInt(key, value).apply();
	}
	
	public static void write(Context context, String SharedPreferencesName, String key, Boolean value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(key, value).apply();
	}
	
	public static String readString(Context context, String SharedPreferencesName, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, "");
	}

	public static int readInt(Context context, String SharedPreferencesName, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, 0);
	}

	public static Boolean readBoolean(Context context, String SharedPreferencesName, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, false);
	}

	public static boolean contains(Context context, String SharedPreferencesName, String key) {
        SharedPreferences sp = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        return sp.contains(key);  
    }
	
	public static Map<String, ?> getAll(Context context, String SharedPreferencesName) {
        SharedPreferences sp = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        return sp.getAll();  
    }
	
	public static void remove(Context context, String SharedPreferencesName, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
		sharedPreferences.edit().remove(key).apply();
	}
	
	public static void clear(Context context, String SharedPreferencesName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    } 
}
