package com.components;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Ravish on 10/27/2014.
 */
public interface Respondable<T> {

    public T respond(JSONArray json, int request);

}
