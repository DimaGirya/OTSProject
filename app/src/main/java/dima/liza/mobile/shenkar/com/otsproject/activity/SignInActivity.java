package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseException;


import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.R;

public class SignInActivity extends AppCompatActivity {
    private static String TAG  = "SignInActivity";
    EditText email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        email = (EditText) findViewById(R.id.SignInEmail);
        password = (EditText) findViewById(R.id.SignInPassword);
    }

    public void onClickGotoSignUp(View view) {
        Intent intent = new Intent(this, SignUpManagerActivity.class);
        startActivity(intent);
    }

    public void SignInClicked(View view) {
        //todo make waiting dialog
        final String emailStr = email.getText().toString();
        final String passwordStr = password.getText().toString();
        ParseUser.logInInBackground(emailStr, passwordStr, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                } else {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("newEmployee");
                    query.whereEqualTo("email", emailStr);
                    query.whereEqualTo("numberPhone", passwordStr);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> employee, ParseException e) {
                            if (e == null) {
                                if(employee.size()==0){
                                }
                                else {

                                }
                            } else {

                            }
                        }
                    });
                    // Signup failed. Look at the ParseException to see what happened.
                }
            }
        });
        // check if user exist
        // check if user is manager or employee
        // if user is employee check if user is new employee
        // if user is new employee pop up to change password
        // authorize log in and go to next screen
    }
}
