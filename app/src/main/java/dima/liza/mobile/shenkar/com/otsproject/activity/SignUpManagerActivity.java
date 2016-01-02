package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import dima.liza.mobile.shenkar.com.otsproject.ManagerAuthorization;
import dima.liza.mobile.shenkar.com.otsproject.R;

public class SignUpManagerActivity extends AppCompatActivity {
    public Context getThis(){
        return this;
    }
    private static String TAG  = "SignUpManagerActivity";
    EditText editTextEmail,editTextPassword,editTextPhone;
    SharedPreferences sharedpreferences;
    public static final String LoginPreferences = "LoginPreferences" ;
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
        Intent intent = new Intent (this,SignInManagerActivity.class);
        startActivity(intent);
    }

    public void signUpManagerClicked(View view) {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String phoneNumber = editTextPhone.getText().toString();
        ManagerAuthorization authorizationLocal = new ManagerAuthorization();
        if(authorizationLocal.signUp(email,password,phoneNumber, this)){
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
                        Toast.makeText(getThis(),"SIGN UP DONE",Toast.LENGTH_LONG).show();
                        // Hooray! Let them use the app now.
                    } else {
                        Toast.makeText(getThis(),"Any parsse error,Try letter!",Toast.LENGTH_LONG).show();
                    }
                }
            });
            // todo Dima or Liza chek if registration succeeded
            Intent intent = new Intent(getThis(), EditTeamActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(this,"Error sign up.Try again leter",Toast.LENGTH_LONG).show();
            return;
        }



    }
}
