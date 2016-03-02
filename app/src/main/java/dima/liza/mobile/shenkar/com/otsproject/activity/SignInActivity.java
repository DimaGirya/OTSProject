package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        //add user name field
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
                    if (user.getBoolean("isManager") == true){
                        //Intent intent = new Intent (this,ManagerTeamTasks.class);
                        //startActivity(intent);
                    } else { // //user is existing employee
                    // Intent intent = new Intent (this,EmployeeTasks.class);
                    // startActivity(intent);
                    }
                } else {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("newEmployee");
                    query.whereEqualTo("email", emailStr);
                    query.whereEqualTo("numberPhone", passwordStr);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> employee, ParseException e) {
                            if (e == null) {
                                if(employee.size()==0){
                                    // //user doesn't exist
                                   Toast.makeText(SignInActivity.this, "Wrong Log-In details", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    ParseObject newEmployee = employee.get(0);
                                    ParseUser user = new ParseUser();
                                    user.setUsername(newEmployee.getString("email")); //temp! need to implement username from input.
                                    user.setPassword(newEmployee.getString("numberPhone")); //temp! need to implement change of password by user
                                    user.setEmail(newEmployee.getString("email"));
                                    user.put("phoneNumber", newEmployee.getString("numberPhone"));
                                    user.put("isManager",false);
                                    user.put("manager", newEmployee.getString("manager"));
                                    // LogIn() //need to implement
                                }
                            } else {
                                Toast.makeText(SignInActivity.this, "Something went wrong, try again later.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    // Signup failed. Look at the ParseException to see what happened.
                }
            }
        });
    }
}
