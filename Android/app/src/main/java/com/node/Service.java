package com.node;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.components.Respondable;
import com.happening.happening.EventsActivity;
import com.happening.happening.LoginActivity;
import com.happening.happening.createAccount;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

/**
 * Created by Ai Le on 10/24/2014.
 */
public class Service {
    public static final String ec2URL = "ec2-54-172-191-175.compute-1.amazonaws.com";
    public static final String authUrl = ec2URL + "/users/auth";
    private static String SESSIONID = null;
    private static String username = null;


    public static final Integer GET = 0x0;
    public static final Integer POST = 0x1;
    public static final Integer DELETE = 0x2;

    public static boolean loginUser(String userName, String passWord, Respondable<Object> instance) {

        try {
            URI postAddress = new URI("http", "", ec2URL, 3000, "/users/auth", "", "");
            String jsonData = new String("{\"username\":\"" + userName + "\",\"userPassword\":\"" + passWord + "\"}");
            new SendRequest().execute(postAddress, jsonData, instance, POST, 0);


        } catch (Exception ex) {

        }

        return true;
    }

    public static boolean signupUser(String userName, String email, String password, Respondable<Object> instance) {

        try {
            URI postAddress = new URI("http", "", ec2URL, 3000, "/users/adduser", "", "");
            String jsonData = new String("{\"username\":\"" + userName + "\",\"userEmail\":\"" + email + "\",\"userPassword\":\"" + password + "\"}");
            Log.wtf("service", jsonData);
            new SendRequest().execute(postAddress, jsonData, instance, POST, 0);
        }

        catch(Exception exe) {

        }
        return true;
    }

    public static boolean loginWithFacebook(String username, Respondable<Object> instance) {

        try {
            URI postAddress = new URI("http", "", ec2URL, 3000, "/users/facebook", "", "");
            String jsonData = new String("{\"username\":\"" + username + "\"}");
            Log.wtf("service", jsonData);
            new SendRequest().execute(postAddress, jsonData, instance, POST, 0);
        }

        catch(Exception exe) {

        }


        return true;
    }

    public static boolean getAllEvents(Respondable<Object> instance) {
        try{
            URI getAddress = new URI("http", "", ec2URL, 3000, "/events/eventlist", "", "");
            String nullString = "";
            new SendRequest().execute(getAddress,  nullString, instance, GET, 0);
        }

        catch(Exception exe) {

        }
        return true;
    }
    public static boolean getMyEvents(Respondable<Object>  instance ) {
        try {
            URI getAddress = new URI("http", "", ec2URL, 3000, "/events/eventlist/"+ username, "", "");
            String jsonData = new String("{\"token\":\"" + SESSIONID + "\"}");
            new SendRequest().execute(getAddress, jsonData, instance, POST, 0);
        } catch (Exception exe) {
            Log.d("service myEvents", exe.getMessage());

        }
        return true;
    }

    public static boolean addEvent(String name, String loc, String date, String time, Respondable<Object> instance) {
        try {
            URI address = new URI("http", "", ec2URL, 3000, "/events/addevent", "", "");
            String jsonData = new String("{\"userToken\":\"" + SESSIONID + "\",\"eventname\":\"" + name + "\",\"location\":\"" + loc + "\",\"date\":\"" + date + "\",\"time\":\"" + time + "\"}");
            Log.wtf("service json", jsonData);
            new SendRequest().execute(address, jsonData, instance, POST, 0);
        }
        catch (Exception exe) {

        }
        return  true;
    }

    public static boolean acceptEvent(String eventid, Respondable<Object> instance, int requestCode) {
        try {
            URI address = new URI("http", "", ec2URL, 3000, "/events/acceptevent/" + eventid, "", "");
            String jsonData = new String("{\"userToken\":\"" + SESSIONID + "\"}");
            Log.wtf("service json", jsonData);
            new SendRequest().execute(address, jsonData, instance, POST, requestCode);
        }
        catch (Exception exe) {

        }
        return  true;
    }




    public static boolean deleteEvent(String id, Respondable<Object> instance) {
        try {
            URI address = new URI("http", "", ec2URL, 3000, "/events/deleteevent/" + id, "", "");
            Log.wtf("service json", address.toString());
            String nildata = "";
            new SendRequest().execute(address, nildata, instance, DELETE, 0);
        }
        catch (Exception exe) {

        }
        return true;
    }

    public static void setSessionId(String sessionId) {
        SESSIONID = sessionId;
        Storage storage = Storage.get();
        storage.addKeyValue("sessionid", sessionId);
    }
    public static void setUsername(String _username) {
        username = _username;
        Storage storage = Storage.get();
        storage.addKeyValue("username", username);
    }

    public static String getUsername() {
        Storage storage = Storage.get();
        username = storage.getKeyValue("username");

        return username;
    }


    public static String getSesionId() {

        Storage storage = Storage.get();
        SESSIONID = storage.getKeyValue("sessionid");

        return SESSIONID;
    }

    public static String getUserID() {

        Storage storage = Storage.get();
        username = storage.getKeyValue("userid");

        return username;
    }

    public static void removeSessionId(Activity application) {
        Storage storage = Storage.get();
        storage.removeKeyValue("sessionid");
    }

}

class SendRequest extends AsyncTask<Object, Object, JSONArray> {

    private static Respondable<Object> respondable;
    private static Integer requestID;
    protected JSONArray doInBackground(Object... params) {

        Integer request = (Integer)params[3];
        requestID = (Integer)params[4];
        if(request == Service.POST) {

            JSONArray json = sendPostData((URI) params[0], (String) params[1], (Respondable<Object>) params[2], requestID);
            return json;
        }
        else if(request == Service.GET) {
            JSONArray json = sendRequest(Service.GET, (URI) params[0], (Respondable<Object>) params[2], requestID);
            return json;
        }
        else if(request == Service.DELETE) {
            JSONArray json = sendRequest(Service.DELETE, (URI) params[0], (Respondable<Object>) params[2], requestID);
            return json;
        }

        return null;
    }

    private JSONArray sendPostData(URI url, String jsonData, Respondable<Object> respondable, int requestID) {
        try {
            this.respondable = respondable;
            this.requestID = requestID;
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost request = new HttpPost(url);
            request.addHeader("content-type", "application/json");
            StringEntity param = new StringEntity(jsonData);
            request.setEntity(param);
            HttpResponse response = httpClient.execute(request);
            Log.wtf("service", response.toString());
            Log.d("service", response.getEntity().toString());

            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();
                JSONArray jsonArr;
                if(responseString.charAt(0) == '{') {

                    JSONObject json = new JSONObject(responseString);
                    jsonArr = new JSONArray();
                    jsonArr.put(json);

                }
                else {
                    jsonArr = new JSONArray(responseString);
                }
                return jsonArr;

            } else {
                response.getEntity().getContent().close();
                Log.wtf("service", statusLine.getStatusCode() + "");

                return null;

            }
        } catch (Exception ex) {
            Log.wtf("service", ex.toString());
            return null;
        }
    }

    private JSONArray sendRequest(Integer type, URI url, Respondable<Object> respondable, Integer requestID) {
        try {
            this.respondable = respondable;
            this.requestID = requestID;
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = null;
            if(type == Service.GET) {
                HttpGet request = new HttpGet(url);

                request.addHeader("content-type", "application/json");
                response = httpClient.execute(request);
            }
            else if(type == Service.DELETE) {
                HttpDelete request = new HttpDelete(url);

                request.addHeader("content-type", "application/json");
                response = httpClient.execute(request);
            }
                Log.wtf("service response tostring", response.toString());
            Log.d("service response get entity", response.getEntity().toString());

            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();
                JSONArray jsonArr = new JSONArray(responseString);
                return jsonArr;

            } else {
                response.getEntity().getContent().close();
                Log.wtf("service status", statusLine.getStatusCode() + "");

                return null;

            }
        } catch (Exception ex) {
            Log.wtf("service exception", ex.toString());
            return null;
        }
    }


    protected void onPostExecute(JSONArray json) {

        this.respondable.respond(json, this.requestID);
    }

}
