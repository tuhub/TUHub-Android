package edu.temple.tuapilogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.*;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import edu.temple.tuapilogin.Models.User;

public class MainActivity extends AppCompatActivity {


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
                String pass = pass1.getText().toString();

                User.signInUser(user, pass, new User.UserRequestListener() {
                    @Override
                    public void onResponse(User user) {
                        kUser = user;
                        tuId = user.getTuID();
                    }

                    @Override
                    public void onError(ANError error) {
                        // Handle error
                        Log.v(MainActivity.class.getSimpleName(), error.getErrorDetail());
                        textView.setText("Invalid Username/Password");
                    }
                });

            }
        });
    }
}