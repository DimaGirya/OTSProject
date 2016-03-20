package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import dima.liza.mobile.shenkar.com.otsproject.ManagerValidation;
import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.SynchronizationService;


public class SignUpManagerActivity extends AppCompatActivity {
    ProgressDialog  pd;
    SharedPreferences teamNameSharedPreferences;
    private static String TAG  = "SignUpManagerActivity";
    EditText editTextEmail,editTextPassword,editTextPhone,editTextName;
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
        //    Toast.makeText(this,"Any  error, See the log!",Toast.LENGTH_LONG).show();
        }
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            if(currentUser.getBoolean("isManager")) {
                Toast.makeText(this, "Welcome back", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ShowTaskManagerActivity.class);
                intent.putExtra("caller", "SignUpManagerActivity");
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(this, "Welcome back", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this,TaskShowEmployeeActivity.class);
                intent.putExtra("caller", "SignUpManagerActivity");
                startActivity(intent);
                finish();
            }
        } else {
            setContentView(R.layout.activity_sign_up_manager);
            editTextEmail = (EditText) findViewById(R.id.editEmailSignUpManager);
            editTextPassword = (EditText) findViewById(R.id.editPasswordSignUpManager);
            editTextPhone = (EditText) findViewById(R.id.editPhoneSignUpManager);
            editTextName = (EditText) findViewById(R.id.editTextManagerName);
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
        String managerName = editTextName.getText().toString();
        ManagerValidation authorizationLocal = new ManagerValidation();
        if(authorizationLocal.signUpValidation(email,password,phoneNumber, this)){
            pd = new ProgressDialog(this);
            pd.setTitle("Adding manager to data base");
            pd.setMessage("Please wait");
            pd.show();
            ParseUser user = new ParseUser();
            user.setUsername(managerName);
            user.setPassword(password);
            user.setEmail(email);
            user.put("phoneNumber", phoneNumber);
            user.put("isManager",true);
            user.put("manager", email);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {    // SIGN UP DONE
                        Toast.makeText(SignUpManagerActivity.this, "Sign up done.Now create team", Toast.LENGTH_LONG).show();
                        createTeam(ParseUser.getCurrentUser());
                    } else {
                        Log.d(TAG, "ParseException:", e);
                        Toast.makeText(SignUpManagerActivity.this, "Sign up failed"+e.getMessage(), Toast.LENGTH_LONG).show();
                        //todo check why sign up failed(already have a user or no internet) and notify user
                        Log.e(TAG,"ParseException:",e);
                        SignUpManagerActivity.this.pd.dismiss();
                    }
                }
            });

        }
    }

    private void createTeam(final ParseUser currentUser) {
        Log.d(TAG, "Create Team start");
        ParseObject newTeam = new ParseObject("Team");
        newTeam.put("TeamName", currentUser.getUsername() + " team");
        newTeam.put("Manager",currentUser.getEmail());
        newTeam.put("listEmployeeUpdate",true);
        Log.d(TAG, "Create Team saveInBackground");
        newTeam.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Create Team teamNameSharedPreferences start");
                    teamNameSharedPreferences = getSharedPreferences("Team",MODE_PRIVATE);
                    SharedPreferences.Editor ed = teamNameSharedPreferences.edit();
                    ed.putString("TeamName", currentUser.getUsername() + " team");
                    ed.putString("Manager", currentUser.getEmail());
                    ed.commit();
                    Log.d(TAG, "Create Team teamNameSharedPreferences finish");
                    Intent intent = new Intent(SignUpManagerActivity.this, EditTeamActivity.class);
                    intent.putExtra("caller", "SignUpManagerActivity");
                    startActivity(intent);
                    Intent serviceIntent = new Intent(SignUpManagerActivity.this, SynchronizationService.class);
                    startService(serviceIntent);
                    pd.dismiss();
                    Log.d(TAG, "Create Team done");
                    finish();
                } else {
                    Log.d(TAG, "Create Team Error",e);
                    //todo
                }
            }
        });

    }
}

