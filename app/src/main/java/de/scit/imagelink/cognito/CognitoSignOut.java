package de.scit.imagelink.cognito;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;

public class CognitoSignOut {

    public static void signOutUser() {
        CognitoUser user = AppHelper.getPool().getCurrentUser();
        user.signOut();

    }
}
