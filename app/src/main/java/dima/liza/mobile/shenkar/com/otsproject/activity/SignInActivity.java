package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import dima.liza.mobile.shenkar.com.otsproject.SynchronizationService;

public class SignInActivity extends AppCompatActivity {
    ProgressDialog  pd;
    private static String TAG  = "SignInActivity";
    EditText email, password;
    final Context context = this;
    String newUsername, newPassword;

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
        //todo make waiting dialog here
        pd = new ProgressDialog(this);
        pd.setTitle("Signing-In");
        pd.setMessage("Please wait");
        pd.show();
        final String emailStr = email.getText().toString();
        final String passwordStr = password.getText().toString();
        ParseUser.logInInBackground(emailStr, passwordStr, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    //user found in Parse Users class.
                    if (user.getBoolean("isManager") == true) {
                        Intent serviceIntent = new Intent(SignInActivity.this, SynchronizationService.class);
                        startService(serviceIntent);
                        Intent intent = new Intent(context, ShowTaskManagerActivity.class);
                        startActivity(intent);
                        pd.dismiss();
                        finish();
                    } else {
                        Intent intent = new Intent(context, TaskShowEmployeeActivity.class);
                        startActivity(intent);
                        Intent serviceIntent = new Intent(SignInActivity.this, SynchronizationService.class);
                        startService(serviceIntent);
                        pd.dismiss();
                        finish();
                    }
                } else {
                    //check if user is in newEmployee Parse class
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("newEmployee");
                    query.whereEqualTo("email", emailStr);
                    query.whereEqualTo("numberPhone", passwordStr);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> employee, ParseException e) {
                            if (e == null) {
                                if (employee.size() == 0) {
                                    //user does not exist
                                    pd.dismiss();
                                    Toast.makeText(SignInActivity.this, "Wrong Log-In details", Toast.LENGTH_LONG).show();
                                } else {
                                    //user found, move to Users Parse class (sign up
                                    final ParseObject newEmployee = employee.get(0);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("enter new username");
                                    final EditText input = new EditText(context);
                                    builder.setView(input);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            newUsername = input.getText().toString();
                                            dialog.dismiss();
                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                            builder2.setTitle("enter new password");
                                            final EditText input2 = new EditText(context);
                                            builder2.setView(input2);
                                            builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    newPassword = input2.getText().toString();
                                                    ParseUser user = new ParseUser();
                                                    user.setUsername(newUsername); //temp, need to implement username from input.
                                                    user.setPassword(newPassword); //temp, need to implement change of password by user
                                                    user.setEmail(newEmployee.getString("email"));
                                                    user.put("phoneNumber", newEmployee.getString("numberPhone"));
                                                    user.put("isManager", false);
                                                    user.put("manager", newEmployee.getString("manager"));
                                                    user.signUpInBackground(new SignUpCallback() {
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                // sign up succeed, delete user from newEmloyee parse class
                                                                // Toast.makeText(SignInActivity.this, "NEW EMPLOYEE signed up", Toast.LENGTH_LONG).show();
                                                                newEmployee.deleteInBackground();
                                                                Intent serviceIntent = new Intent(SignInActivity.this, SynchronizationService.class);
                                                                startService(serviceIntent);
                                                                Intent intent = new Intent(context, TaskShowEmployeeActivity.class);
                                                                startActivity(intent);
                                                                pd.dismiss();
                                                                finish();
                                                            } else {
                                                                // Sign up didn't succeed
                                                                pd.dismiss();
                                                                Toast.makeText(SignInActivity.this, "signup failed", Toast.LENGTH_LONG).show();
                                                                Log.d(TAG, "ParseException", e);
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                            builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            builder2.show();
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder.show();
                                }
                            } else {
                                pd.dismiss();
                                Toast.makeText(SignInActivity.this, "Something went wrong, try again later.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}


