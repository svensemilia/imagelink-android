package de.scit.imagelink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import de.scit.imagelink.cognito.CognitoSignIn;

public class SignInActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;

    public static final String EXTRA_INPUT_USER = "user";
    public static final String EXTRA_INPUT_PWD = "pwd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        String lastUser = getIntent().getStringExtra(EXTRA_INPUT_USER);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        if (lastUser != null) {
            username.setText(lastUser);
        }
    }

    public void login(View view) {
        Intent intent = new Intent();
        String inputUser = username.getText().toString();
        String inputPwd = password.getText().toString();

        intent.putExtra(EXTRA_INPUT_USER,inputUser);
        intent.putExtra(EXTRA_INPUT_PWD,inputPwd);
        setResult(CognitoSignIn.CODE_SIGN_IN_REQUEST, intent);
        finish();
    }
}
