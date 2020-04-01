package de.scit.imagelink.cognito;

public interface SignInListener {
    void onSignInSuccess();

    void onSignInFailure();
}
