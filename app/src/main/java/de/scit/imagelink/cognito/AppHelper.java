package de.scit.imagelink.cognito;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoRefreshToken;
import com.amazonaws.regions.Regions;

public class AppHelper {
    private static final String TAG = "AppHelper";

    private static AppHelper appHelper;
    private static CognitoUserPool userPool;
    private static String user;

    private static final String userPoolId = "eu-central-1_s0wU2o3un";
    private static final String clientId = "3qngaeqht61ie6jhutnv6918e7";
    private static final String clientSecret = null;
    private static final Regions cognitoRegion = Regions.EU_CENTRAL_1;

    // User details from the service
    private static CognitoUserSession currSession;
    private static CognitoUserDetails userDetails;

    public static void init(Context context) {
        if (appHelper != null && userPool != null) {
            return;
        }

        if (appHelper == null) {
            appHelper = new AppHelper();
        }

        if (userPool == null) {
            userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);
        }
    }

    public static CognitoUserPool getPool() {
        return userPool;
    }

    public static  CognitoUserSession getCurrSession() { return currSession; }

    public static String getCurrUser() { return user; }

    public static void setCurrSession(CognitoUserSession session) {
        currSession = session;
    }

    public static void setUser(String newUser) { user = newUser; }

    public static String getIdToken() {
        if (currSession == null) {
            throw new IllegalStateException("You are not associated with any user session");
        }

        if (!currSession.isValid()) {
            Log.i(TAG, "Current session is not valid");
            //refresh session if neccessary
            //CognitoRefreshToken refresh = currSession.getRefreshToken();
        }
        Log.i(TAG, currSession.getIdToken().getJWTToken());
        return currSession.getIdToken().getJWTToken();
    }
}
