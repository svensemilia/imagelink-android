package de.scit.imagelink;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import de.scit.imagelink.common.DefaultResponseHandler;
import de.scit.imagelink.rest.ImageRestApi;
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

        checkServer(null);
    }

    public void checkServer(View v) {
        ImageRestApi.getServerState(serverStateHandler);
    }

    private void checkAPI() {
        try {
            ImageRestApi.getAPIHealth(apiHealthHandler);
        } catch (IllegalStateException e) {
            Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
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
                ImageRestApi.postServerState("start", serverStartStopHandler);
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            //stop server
            Log.i("Status", "stop server");
            try {
                ImageRestApi.postServerState("stop", serverStartStopHandler);
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    AsyncHttpResponseHandler serverStateHandler = new JsonHttpResponseHandler() {
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
                    checkAPI();
                } else {
                    stateListener.setServerRunning(false, null);
                    stateListener.setApiOnline(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    AsyncHttpResponseHandler apiHealthHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i("TEST", "OnSuccess JsonObj");
            Log.i("TEST", response.toString());
            if (statusCode == 200) {
                stateListener.setApiOnline(true);
            } else {
                stateListener.setApiOnline(false);
            }
        }
    };

    AsyncHttpResponseHandler serverStartStopHandler = new DefaultResponseHandler();
}
