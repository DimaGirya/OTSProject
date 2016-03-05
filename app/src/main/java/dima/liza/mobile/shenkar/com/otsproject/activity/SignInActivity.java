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
import com.parse.SignUpCallback;


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
        signIn(emailStr, passwordStr);
    }

    void signIn( final String emailAdd, final String passw){
        ParseUser.logInInBackground(emailAdd, passw, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // user found in Parse Users class.
                    if (user.getBoolean("isManager") == true){
                        //TODO: uncomment lines 56-57 when ManagerTeamTasks is ready
                        /* Intent intent = new Intent (this,ManagerTeamTasks.class);
                        startActivity(intent); */
                    } else {
                        //TODO: uncomment lines 60-61 when EmployeeTasks is ready
                        /* Intent intent = new Intent (this,EmployeeTasks.class);
                        startActivity(intent); */
                    }
                } else {
                    //check if user is in newEmployee Parse class
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("newEmployee");
                    query.whereEqualTo("email", emailAdd);
                    query.whereEqualTo("numberPhone", passw);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> employee, ParseException e) {
                            if (e == null) {
                                if(employee.size()==0){
                                    //user does not exist
                                    Toast.makeText(SignInActivity.this, "Wrong Log-In details", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    //user found, move to Users Parse class (sign up
                                    final ParseObject newEmployee = employee.get(0);
                                    ParseUser user = new ParseUser();
                                    user.setUsername(newEmployee.getString("email")); //temp, need to implement username from input.
                                    user.setPassword(newEmployee.getString("numberPhone")); //temp, need to implement change of password by user
                                    user.setEmail(newEmployee.getString("email"));
                                    user.put("phoneNumber", newEmployee.getString("numberPhone"));
                                    user.put("isManager", false);
                                    user.put("manager", newEmployee.getString("manager"));
                                    user.signUpInBackground(new SignUpCallback() {
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                // sign up succeed, delete user from newEmloyee parse class
                                                newEmployee.deleteInBackground();
                                            } else {
                                                // Sign up didn't succeed
                                                Toast.makeText(SignInActivity.this, "signup failed", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(SignInActivity.this, "Something went wrong, try again later.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
