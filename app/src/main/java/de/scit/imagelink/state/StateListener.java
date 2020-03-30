package de.scit.imagelink.state;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Switch;

import de.scit.imagelink.R;

public class StateListener {

    private boolean isServerRunning;
    private boolean isApiOnline;
    private View serverButton;
    private View apiButton;
    private Switch switchView;
    private Context ctx;

    public StateListener(Context ctx, View serverButton, View apiButton, Switch switchV) {
        this.serverButton = serverButton;
        this.apiButton = apiButton;
        this.switchView = switchV;
        this.ctx = ctx;
    }

    public void setServerRunning(boolean isServerRunning) {
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
