package com.node;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

/**
 * Created by Ravish on 10/29/2014.
 */

public class Storage {
    private static final String file = "happening";
    static SharedPreferences prefs;
    static SharedPreferences.Editor editor;
    Storage storage;
    App context;

    private static Storage instance;

    public Storage() {

        context = App.get();
        prefs = context.getSharedPreferences(file, context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static Storage get() {

        if(instance == null) {
            instance = new Storage();
        }
        return instance;

    }


    public void addKeyValue(String key, String value) {

        editor.putString(key, value);
        editor.commit();
    }

    public String getKeyValue(String key) {

        return prefs.getString(key, null);

    }

    public void removeKeyValue(String key) {

        if(prefs.contains(key))
        {
            Log.wtf("service key", prefs.getString(key, null) );
            editor.remove(key);
            editor.commit();
        }

    }


}
