package de.scit.imagelink.common;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DefaultResponseHandler extends JsonHttpResponseHandler {

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.i("TEST", "OnSuccess JsonObj");
        Log.i("TEST", response.toString());
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
        if (response != null) {
            Log.i("TEST","Hello Hello");
            Log.i("TEST", response.toString());
        }
    }
}
