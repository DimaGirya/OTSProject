package dima.liza.mobile.shenkar.com.otsproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivityManager extends AppCompatActivity {
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
        sharedpreferences = getPreferences(MODE_PRIVATE);
        sharedpreferences.getBoolean(IfLoggedIn,ifLogIn);
        if(!ifLogIn) {
            setContentView(R.layout.activity_sign_up_activity_manager);
            editTextEmail = (EditText) findViewById(R.id.editEmailSignUpManager);
            editTextPassword = (EditText) findViewById(R.id.editPasswordSignUpManager);
            editTextPhone = (EditText) findViewById(R.id.editPhoneSignUpManager);
        }
        else{
            // user loget in move to next activity
        }
    }

    public void signInManagerClickedInSignUp(View view) {
        String email = editTextEmail.getText().toString();
        // todo liza: check if email valid
        String password = editTextPassword.getText().toString();
        if(password.length()<5){
            Toast.makeText(this,"Password must by at lest 6 charecters",Toast.LENGTH_LONG).show();
            return;
        }
        String phoneNumber = editTextPhone.getText().toString();
        // todo liza: check if telephone number valid
        if (phoneNumber.length() > 10 | phoneNumber.length() < 8) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_LONG).show();
        }
        AuthorizationLocal authorizationLocal = new AuthorizationLocal();
        if(authorizationLocal.signUp(email,password,phoneNumber)){

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Password,password);
            editor.putString(Phone,phoneNumber);
            editor.putString(Email,email);
            editor.putBoolean(IfLoggedIn,true);
            editor.commit();
        }
        else{
            Toast.makeText(this,"Error sign up.Try again leter",Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void signUpManagerClicked(View view) {

    }
}
