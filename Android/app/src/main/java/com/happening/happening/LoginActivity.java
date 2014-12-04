package com.happening.happening;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.components.*;
import com.components.Dialog;
import com.facebook.FacebookFragment;
import com.facebook.Session;
import com.node.*;
import com.node.Service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.Callable;


public class LoginActivity extends FragmentActivity implements Respondable<Object>, FacebookFragment.OnFragmentInteractionListener {
    Button login;
    TextView signUp;
    TextView skip;
    static LoginActivity instance;
    public FacebookFragment fragment;
    public ProgressDialog progressIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String Sessionid = Service.getSesionId();
        String username = Service.getUsername();

        String requestID = null;
        Uri intentURI = getIntent().getData();
        if (intentURI != null) {
            String requestIdParam = intentURI.getQueryParameter("request_ids");
            if (requestIdParam != null) {
                String array[] = requestIdParam.split(",");
                requestID = array[0];
                Log.i("service intent uri", requestID);
            }
        }

        if (Sessionid != null) {
            Navigation.get().navigateLogin(this, requestID);
        }


        Session session = Session.getActiveSession();
        if (session != null)
            Log.wtf("session service", session.getState().toString());
        else
            Log.wtf("session service", "is null");

        setContentView(R.layout.activity_login);
        instance = this;

        if (savedInstanceState == null) {
            fragment = new FacebookFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
        } else {
            fragment = (FacebookFragment) getSupportFragmentManager().findFragmentByTag("Facebook");
        }

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.wtf("service bundle", savedInstanceState.getString("this", "are"));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {


        outState.putString("this", "is");
        super.onSaveInstanceState(outState);
        Log.wtf("service bundle", "saving " + outState.getString("this"));
    }

    public static LoginActivity getInstance() {
        if (instance == null) {
            instance = new LoginActivity();
        }
        return instance;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public Object respond(JSONArray jsonArr, int requestCode) {
        try {
            JSONObject json = jsonArr.getJSONObject(0);

            if (json.has("msg")) {
                throw new Exception("Invalid user found");
            }
            getInstance().progressIndicator.dismiss();
            Service.setSessionId(json.getString("token"));
            Service.setUsername(json.getString("username"));
            //Transition to events activity
            Navigation.get().navigateLogin(this, null);
        } catch (Exception exe) {
            AlertDialog dialog = Dialog.createSimpleDialog("The Username or Password entered is incorrect", "", LoginActivity.this);
        }
        return null;
    }
}
