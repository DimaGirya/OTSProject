package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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
import dima.liza.mobile.shenkar.com.otsproject.UpdateData;
import dima.liza.mobile.shenkar.com.otsproject.Validation;

public class SignInActivity extends AppCompatActivity {
    private ProgressDialog  pd;
    private static String TAG  = "SignInActivity";
    private EditText email, password;
    final Context context = this;
    private  String newUsername, newPassword;

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
        pd = new ProgressDialog(this);
        pd.setTitle(getString(R.string.signingIn));
        pd.setMessage(getString(R.string.pleaseWait));
        pd.show();
        final UpdateData updateData = UpdateData.getInstance();
        final String emailStr = email.getText().toString();
        final String passwordStr = password.getText().toString();
        ParseUser.logInInBackground(emailStr, passwordStr, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    //user found in Parse Users class.
                    if (user.getBoolean("isManager") == true) {
                        updateData.updateTaskList(SignInActivity.this, true);
                        updateData.getLocationFromParse(SignInActivity.this);
                        Intent serviceIntent = new Intent(SignInActivity.this, SynchronizationService.class);
                        startService(serviceIntent);
                        Intent intent = new Intent(context, ShowTaskManagerActivity.class);
                        startActivity(intent);
                        pd.dismiss();
                        finish();
                    } else {
                        updateData.updateTaskList(SignInActivity.this, false);
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
                                    Toast.makeText(SignInActivity.this, R.string.wrongLongInDetails, Toast.LENGTH_LONG).show();
                                } else {
                                    //user found, move to Users Parse class (sign up
                                    final ParseObject newEmployee = employee.get(0);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle(R.string.enterNewUsername);
                                    final EditText input = new EditText(context);
                                    builder.setView(input);
                                    builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            newUsername = input.getText().toString();
                                            dialog.dismiss();
                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                            builder2.setTitle(R.string.enterNewPassword);
                                            final EditText input2 = new EditText(context);
                                            input2.setInputType((InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
                                            builder2.setView(input2);
                                            builder2.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    newPassword = input2.getText().toString();
                                                    ParseUser user = new ParseUser();
                                                    user.setUsername(newUsername);
                                                    user.setPassword(newPassword);
                                                    if(!Validation.passwordValidation(newPassword,SignInActivity.this)){
                                                        pd.dismiss();
                                                      return;
                                                    }
                                                    user.setEmail(newEmployee.getString("email"));
                                                    user.put("phoneNumber", newEmployee.getString("numberPhone"));
                                                    user.put("isManager", false);
                                                    user.put("manager", newEmployee.getString("manager"));
                                                    user.signUpInBackground(new SignUpCallback() {
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                Toast.makeText(context,getString(R.string.youBeenAddetToTeam)+ newEmployee.getString("manager"),Toast.LENGTH_LONG).show();
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
                                                                Toast.makeText(SignInActivity.this, R.string.signupFailed, Toast.LENGTH_LONG).show();
                                                                Log.d(TAG, "ParseException", e);
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                            builder2.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            builder2.show();
                                        }
                                    });
                                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder.show();
                                }
                            } else {
                                pd.dismiss();
                                Toast.makeText(SignInActivity.this, R.string.somethingWentWrong, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}


