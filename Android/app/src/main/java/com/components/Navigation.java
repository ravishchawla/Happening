package com.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.happening.happening.EventsActivity;
import com.happening.happening.LoginActivity;
import com.node.App;

/**
 * Created by Ravish on 10/29/2014.
 */
public class Navigation {

    private static Navigation instance;
    public static Navigation get() {
        if(instance == null) {
            instance = new Navigation();
        }

        return instance;
    }

    public void navigateLogin(Activity context, String requestID) {

        Intent intent = new Intent(App.get(), EventsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("requestID", requestID);
        context.startActivity(intent);
        context.finish();

    }

    public void navigateSignup(Activity login, Activity signup) {

        Intent intent = new Intent(App.get(), EventsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        signup.startActivity(intent);
        login.finish();
        signup.finish();
    }

    public void navigateToLogin(Activity context) {

        Intent intent = new Intent(App.get(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
        context.finish();

    }

}
