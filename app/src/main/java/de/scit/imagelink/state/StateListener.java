package de.scit.imagelink.state;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import de.scit.imagelink.R;
import de.scit.imagelink.de.scit.imagelink.rest.ImageRestApi;

public class StateListener {

    private boolean isServerRunning;
    private boolean isApiOnline;
    private View serverButton;
    private View apiButton;
    private Switch switchView;
    private TextView tv;
    private Context ctx;

    public StateListener(Context ctx, View serverButton, View apiButton, Switch switchV, TextView tv) {
        this.serverButton = serverButton;
        this.apiButton = apiButton;
        this.switchView = switchV;
        this.tv = tv;
        this.ctx = ctx;
    }

    public void setServerRunning(boolean isServerRunning, String ip) {
        if (this.isServerRunning != isServerRunning) {
            refreshServerButton(isServerRunning);
        }
        this.isServerRunning = isServerRunning;

        if (!switchView.isEnabled()) {
            switchView.setEnabled(true);
        }

        if (isServerRunning != switchView.isChecked()) {
            switchView.setChecked(!switchView.isChecked());
        }

        if (isServerRunning && ip != null) {
            tv.setText(ip);
            ImageRestApi.setServerIP(ip);
        } else {
            tv.setText("-");
            ImageRestApi.setServerIP(null);
        }
    }

    public void setApiOnline(boolean isApiOnline) {
        if (this.isApiOnline != isApiOnline) {
            refreshApiButton(isApiOnline);
        }
        this.isApiOnline = isApiOnline;
    }

    private void refreshServerButton(boolean isServerRunning) {
        Drawable dr;
        if (isServerRunning) {
            dr = ctx.getResources().getDrawable(R.drawable.circle_green);
        } else {
            dr = ctx.getResources().getDrawable(R.drawable.circle_red);
        }
        serverButton.setBackground(dr);
    }

    private void refreshApiButton(boolean isApiOnline) {
        Drawable dr;
        if (isApiOnline) {
            dr = ctx.getResources().getDrawable(R.drawable.circle_green);
        } else {
            dr = ctx.getResources().getDrawable(R.drawable.circle_red);
        }
        apiButton.setBackground(dr);
    }
}
