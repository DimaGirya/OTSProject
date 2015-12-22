package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import dima.liza.mobile.shenkar.com.otsproject.AuthorizationLocal;
import dima.liza.mobile.shenkar.com.otsproject.R;

public class SignUpManagerActivity extends AppCompatActivity {
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
        sharedpreferences = getSharedPreferences(LoginPreferences,MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        ifLogIn =  sharedpreferences.getBoolean(IfLoggedIn,false);
        if(!ifLogIn) {
            setContentView(R.layout.activity_sign_up_manager);
            editTextEmail = (EditText) findViewById(R.id.editEmailSignUpManager);
            editTextPassword = (EditText) findViewById(R.id.editPasswordSignUpManager);
            editTextPhone = (EditText) findViewById(R.id.editPhoneSignUpManager);
        }
        else{
            Toast.makeText(this, "Welcome back", Toast.LENGTH_LONG).show();
            Intent intent = new Intent (this,EditTeamActivity.class);
            startActivity(intent);
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
        AuthorizationLocal authorizationLocal = new AuthorizationLocal();
        if(authorizationLocal.signUp(email,password,phoneNumber, this)){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Password,password);
            editor.putString(Phone,phoneNumber);
            editor.putString(Email,email);
            editor.putBoolean(IfLoggedIn, true);
            editor.commit();
            Toast.makeText(this,"Sign up complete.Welcome ",Toast.LENGTH_LONG).show();
            Intent intent = new Intent (this,EditTeamActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(this,"Error sign up.Try again leter",Toast.LENGTH_LONG).show();
            return;
        }



    }
}
