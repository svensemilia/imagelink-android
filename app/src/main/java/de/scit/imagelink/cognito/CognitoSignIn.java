package de.scit.imagelink.cognito;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

import java.util.ArrayList;
import java.util.List;

import de.scit.imagelink.SignInActivity;

public class CognitoSignIn {
    private static final String TAG = "CognitoSignIn";

    public static final int CODE_SIGN_IN_REQUEST = 1;

    private static Activity activityCtx;
    private static AuthenticationContinuation continuation;
    private static String username;
    private static List<SignInListener> signInListeners;

    public static void init(Activity activityCtx) {
        CognitoSignIn.activityCtx = activityCtx;
        continuation = null;
        signInListeners = new ArrayList<>();
    }

    public static void registerListener(SignInListener listener) {
        signInListeners.add(listener);
    }

    public static void signInLastUser() {
        CognitoUser user = AppHelper.getPool().getCurrentUser();
        username = user.getUserId();
        if(username != null) {
            AppHelper.setUser(username);
            Log.i(TAG, "User found " + username);
        }
        user.getSessionInBackground(authenticationHandler);
    }

    public static void continueSignIn(String username, String password) {
        Log.i(TAG, "Continuation: " + username + " " + password);
        CognitoSignIn.username = username;
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    private static void notifySuccessToListeners() {
        if (signInListeners == null) {
            return;
        }
        for (SignInListener listener : signInListeners) {
            listener.onSignInSuccess();
        }
    }

    private static void notifyFailureToListeners() {
        if (signInListeners == null) {
            return;
        }
        for (SignInListener listener : signInListeners) {
            listener.onSignInFailure();
        }
    }

    private static AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.d(TAG, " -- Auth Success");
            AppHelper.setCurrSession(cognitoUserSession);
            //launchUser();
            notifySuccessToListeners();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation continuation, String username) {
            Log.i(TAG, "Auth details");
            //send username username
            CognitoSignIn.continuation = continuation;
            Intent intent=new Intent(activityCtx, SignInActivity.class);
            activityCtx.startActivityForResult(intent, CODE_SIGN_IN_REQUEST);// Activity is started with requestCode 2
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            Log.i(TAG, "MFA not implemented");
        }

        @Override
        public void onFailure(Exception e) {
            Log.i(TAG, "Sign in failed");
            notifyFailureToListeners();
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            /**
             * For Custom authentication challenge, implement your logic to present challenge to the
             * user and pass the user's responses to the continuation.
             */
            Log.i(TAG, "Challenge called");
            if (continuation != null) {
                Log.i(TAG, continuation.getChallengeName());
            }
        }
    };
}
