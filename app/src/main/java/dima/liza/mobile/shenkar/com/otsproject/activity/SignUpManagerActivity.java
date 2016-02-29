package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import dima.liza.mobile.shenkar.com.otsproject.ManagerValidation;
import dima.liza.mobile.shenkar.com.otsproject.R;

public class SignUpManagerActivity extends AppCompatActivity {
    ProgressDialog  pd;
    private static String TAG  = "SignUpManagerActivity";
    EditText editTextEmail,editTextPassword,editTextPhone;
    public static final String IfLoggedIn = "ifLoggedIn";
    public static final String Password = "password";
    public static final String Phone = "phoneKey";
    public static final String Email = "emailKey";
    Boolean ifLogIn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       try {
            Parse.initialize(this);
        }
        catch (Exception e){
            Log.d(TAG,"Exception:",e);
            Toast.makeText(this,"Any  error, See the log!",Toast.LENGTH_LONG).show();
        }
        ParseUser  currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "Welcome back", Toast.LENGTH_LONG).show();
            Intent intent = new Intent (this,EditTeamActivity.class);
            startActivity(intent);
        } else {
            // show the signup or login screen
            setContentView(R.layout.activity_sign_up_manager);
            editTextEmail = (EditText) findViewById(R.id.editEmailSignUpManager);
            editTextPassword = (EditText) findViewById(R.id.editPasswordSignUpManager);
            editTextPhone = (EditText) findViewById(R.id.editPhoneSignUpManager);
        }

    }

    public void signInManagerClickedInSignUp(View view) {
        Intent intent = new Intent (this,SignInActivity.class);
        startActivity(intent);
    }

    public void signUpManagerClicked(View view) {

        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String phoneNumber = editTextPhone.getText().toString();
        ManagerValidation authorizationLocal = new ManagerValidation();
        if(authorizationLocal.signUpValidation(email,password,phoneNumber, this)){
            pd = new ProgressDialog(this);
            pd.setTitle("Adding manager to data base");
            pd.setMessage("Please wait");
            pd.show();
            ParseUser user = new ParseUser();
            user.setUsername(email);
            user.setPassword(password);
            user.setEmail(email);
            user.put("phoneNumber", phoneNumber);
            user.put("isManager",true);
            user.put("manager", email);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {    // SIGN UP DONE
                        Toast.makeText(SignUpManagerActivity.this, "SIGN UP DONE", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUpManagerActivity.this, EditTeamActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d(TAG, "ParseException:", e);
                        Toast.makeText(SignUpManagerActivity.this, "Sign up failed", Toast.LENGTH_LONG).show();
                        //todo check why sign up failed(already have a user or no internet) and notify user
                        SignUpManagerActivity.this.pd.dismiss();
                    }
                }
            });
        }
    }
}
