package de.scit.imagelink;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import de.scit.imagelink.de.scit.imagelink.rest.ImageRestApi;
import de.scit.imagelink.state.StateListener;

public class StatusActivity extends AppCompatActivity {

    private StateListener stateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        View serverButton = findViewById(R.id.button);
        View apiButton = findViewById(R.id.button2);
        Switch switchV = findViewById(R.id.switch1);
        TextView tv = findViewById(R.id.ip);

        stateListener = new StateListener(this, serverButton, apiButton, switchV, tv);

        //check server status ...
        checkServer(null);
    }

    public void checkServer(View v) {
        ImageRestApi.getServerState(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("TEST", "OnSuccess JsonObj");
                Log.i("TEST", response.toString());
                try {
                    JSONObject state = response.getJSONObject("state");
                    int stateCode = state.getInt("Code");
                    String ip = null;
                    if (!response.isNull("ip")) {
                        ip = response.getString("ip");
                    }

                    if (stateCode == 16) {
                        stateListener.setServerRunning(true, ip);
                    } else {
                        stateListener.setServerRunning(false, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkAPI() {

    }

    public void startStopServer(View view) {
        if (!(view instanceof Switch)) {
            Log.e("Status", "View is not of type Switch");
        }
        Switch sw = (Switch) view;
        if (sw.isChecked()){
            //start server
            Log.i("Status", "start server");
            try {
                ImageRestApi.postServerState("start", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i("TEST", "OnSuccess JsonObj");
                        Log.i("TEST", response.toString());
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                        Log.i("TEST", response.toString());
                    }
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            //stop server
            Log.i("Status", "stop server");
            try {
                ImageRestApi.postServerState("stop", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i("TEST", "OnSuccess JsonObj");
                        Log.i("TEST", response.toString());
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                        Log.i("TEST", response.toString());
                    }
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
