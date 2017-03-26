package edu.temple.tuhub;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;

import edu.temple.tuhub.*;
import edu.temple.tuhub.models.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public String user, tuId; //3 major strings for our application, the username, password and tuId
    User kUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());

        AndroidNetworking.initialize(getApplicationContext());
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText user1 = (EditText) findViewById(R.id.tu_username);
                EditText pass1 = (EditText) findViewById(R.id.editText2);

                user = user1.getText().toString();
                final String pass = pass1.getText().toString();
                User.signInUser(user, pass, new User.UserRequestListener() {
                    @Override
                    public void onResponse(User user) {
                        kUser = user;
                        final String tuId = user.getTuID();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user", kUser.getUsername());
                        intent.putExtra("tuId", tuId);
                        intent.putExtra("password", pass);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(ANError error) {
                        // Handle error
                        textView.setText("Invalid Username/Password");
                    }
                });

            }
        });
    }

}

