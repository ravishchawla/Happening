package com.happening.happening;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.components.Background;
import com.components.Dialog;
import com.components.EventsAdapter;
import com.components.EventsCreationDialog;
import com.components.Navigation;
import com.components.Respondable;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.FriendPickerActivity;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.node.App;
import com.node.Service;
import com.node.Storage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EventsActivity extends Activity implements Respondable<Object> {

    ListView listView;
    ImageView imageView;
    public static ArrayList<JSONObject> jsonList;
    private static EventsActivity instance;
    public static boolean isCheckedOnAllEvents = true;
    private static boolean isSkip = false;
    private List<GraphUser> friendsList;

    public static final int FRIENDS_SELECT = 0x0;
    public static final int FRIENDS_SELECTED = 0x1;
    private String requestID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        instance = this;

        requestID = getIntent().getStringExtra("requestID");

        if (requestID != null) {
            Log.wtf("service request events", requestID);
            getRequestData(requestID);
        }

        imageView = (ImageView) findViewById(R.id.eventsImageBackground);
        Background.loadBlurredImage(imageView, BitmapFactory.decodeResource(getResources(), R.drawable.blank));

        Intent intent = getIntent();
        isSkip = intent.getBooleanExtra("skip", false);

        listView = (ListView) findViewById(R.id.eventsListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new EventsCreationDialog(EventsActivity.this, i);
            }
        });
        getData();


    }

    public static EventsActivity getInstance() {
        if (instance == null) {
            instance = new EventsActivity();
        }
        return instance;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.events, menu);

        MenuItem item = menu.getItem(1);
        if (isSkip) {
            item.setVisible(false);
        }

        Log.wtf("service menu item", item.getTitle().toString());
        item.setChecked(true);
        isCheckedOnAllEvents = item.isChecked();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.events_action_logout:
                Service.removeSessionId(EventsActivity.this);
                Session.getActiveSession().closeAndClearTokenInformation();
                Navigation.get().navigateToLogin(EventsActivity.this);
                return true;
            case R.id.events_action_myEvents:

                if (item.isChecked()) {
                    item.setTitle("All Events");
                    item.setChecked(false);
                    isCheckedOnAllEvents = item.isChecked();
                    getData();
                    getActionBar().setTitle("My Happenings");
                    return true;
                } else {
                    item.setTitle("My Events");
                    item.setChecked(true);
                    isCheckedOnAllEvents = item.isChecked();
                    getActionBar().setTitle("Happenings");
                    getData();
                    return true;
                }


            case R.id.events_action_add:
                new EventsCreationDialog(this);
                return true;
        }
        return false;
    }

    public void startFriendPickerActivity(String eventname, String description, String location, String _id) {
        Intent intent = new Intent();

        String facebookToken = Storage.get().getKeyValue("facebookAccessToken");
        AccessToken accessToken = AccessToken.createFromExistingAccessToken(facebookToken, null, null, null, null);

     /*       intent.setData(data);
            intent.setClass(getInstance(), FriendPickerActivity.class);
            startActivityForResult(intent, requestcode);

     */

        sendRequestDialog(eventname, description, location, _id);
    }


    public void sendRequestDialog(String eventname, String description, String location, String _id) {
        Bundle params = new Bundle();
        params.putString("message", "You have been invited to " + eventname + " at " + location);
        params.putString("data", "{\"eventname\":\"" + eventname + "\", \"description\":\"" + description + "\", \"id\":\"" + _id + "\"}");

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(EventsActivity.this,
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error != null) {
                            if (error instanceof FacebookOperationCanceledException) {
                                Toast.makeText(getApplicationContext().getApplicationContext(),
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext().getApplicationContext(),
                                        "Network Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            final String requestId = values.getString("request");
                            if (requestId != null) {
                                Toast.makeText(getApplicationContext().getApplicationContext(),
                                        "Request sent",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext().getApplicationContext(),
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                })
                .build();
        requestsDialog.show();
    }


    private void getRequestData(final String requestID) {

        Log.wtf("service requst data", "i am here");
        Request request = new Request(Session.getActiveSession(), requestID, null, HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                GraphObject graphObject = response.getGraphObject();
                FacebookRequestError error = response.getError();
                String message = "Incoming request";

                if (graphObject != null) {
                    Log.wtf("service graph object", graphObject.toString());

                    String fromUser = "";
                    String stateMessage = "";
                    String eventid = "";
                    if (graphObject.getProperty("message") != null) {
                        try {
                            String dataJson = (String) graphObject.getProperty("data");
                            JSONObject data = new JSONObject(dataJson);
                            Log.wtf("sevice json data", data.toString());
                            eventid = data.getString("id");
                            Log.wtf("service eventid", eventid);
                            JSONObject from = (JSONObject) graphObject.getProperty("from");
                            fromUser = from.getString("name");
                            Log.wtf("json object fromuser", fromUser);
                            stateMessage = (String) graphObject.getProperty("message");
                            Log.wtf("json object message", stateMessage);
                        } catch (Exception exe) {
                            Log.wtf("service exception", exe.getMessage());
                        }

                    }

                    final String _eventID = eventid;
                    android.app.AlertDialog.Builder builder = new AlertDialog.Builder(EventsActivity.this);
                    builder.setTitle("Invitation from " + fromUser);
                    builder.setMessage(stateMessage);
                    builder.setCancelable(true);
                    builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Request request = new Request(Session.getActiveSession(),
                                    requestID, null, HttpMethod.DELETE, new Request.Callback() {

                                @Override
                                public void onCompleted(Response response) {
                                    // Show a confirmation of the deletion
                                    // when the API call completes successfully.

                                    //  Service.acceptEvent(_eventID, EventsActivity.getInstance(), 1);
                                    Toast.makeText(getApplicationContext(), "Request Accepted", Toast.LENGTH_SHORT).show();
                                }
                            });
                            // Execute the request asynchronously.
                            Request.executeBatchAsync(request);

                            dialogInterface.cancel();
                        }
                    });

                    builder.setNeutralButton("Tentative", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Request unconfirmed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


                    builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Request Declined",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    ;

                } else {
                    Log.wtf("graph object service", "is null");
                }
            }
        });

        request.executeAsync();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == FRIENDS_SELECTED) {


        }


    }

    public boolean getData() {
        ProgressBar bar = (ProgressBar) findViewById(R.id.eventsProgressBar);
        bar.setVisibility(View.VISIBLE);

        if (isCheckedOnAllEvents) {
            Service.getAllEvents(this);
            return true;
        } else {
            Service.getMyEvents(this);
            return true;
        }
    }

    public Object respond(JSONArray json, int requestCode) {

        if (requestCode == 0) {
            //Log.wtf("service", json.toString());
            ArrayList<JSONObject> jsonList = null;
            try {
                jsonList = new ArrayList<JSONObject>();
                for (int i = 0; i < json.length(); i++) {
                    jsonList.add(json.getJSONObject(i));
                }
                this.jsonList = jsonList;

            } catch (Exception exe) {
                Log.wtf("service in events actiivty", exe.toString());
            }
            ArrayAdapter<JSONObject> adap = new EventsAdapter(getApplicationContext(), jsonList);
            listView.setAdapter(adap);
            ProgressBar bar = (ProgressBar) findViewById(R.id.eventsProgressBar);
            bar.setVisibility(View.GONE);
            return json;

        } else {
            Toast.makeText(getApplicationContext(), "Request Accepted", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public List<GraphUser> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<GraphUser> friendsList) {
        this.friendsList = friendsList;
    }


}
