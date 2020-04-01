package de.scit.imagelink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.scit.imagelink.cognito.CognitoSignIn;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


    }

    public void login(View view) {
        Intent intent = new Intent();
        intent.putExtra("user","sven_carlin@arcor.de");
        intent.putExtra("pwd","admin090");
        setResult(CognitoSignIn.CODE_SIGN_IN_REQUEST, intent);
        finish();
    }
}
