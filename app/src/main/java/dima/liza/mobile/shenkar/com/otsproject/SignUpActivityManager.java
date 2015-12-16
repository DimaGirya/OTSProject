package dima.liza.mobile.shenkar.com.otsproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SignUpActivityManager extends AppCompatActivity {
    EditText editTextEmail,editTextPassword,editTextPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activity_manager);
        editTextEmail = (EditText) findViewById(R.id.editEmailSignUpManager);
        editTextPassword = (EditText) findViewById(R.id.editPasswordSignUpManager);
        editTextPhone = (EditText) findViewById(R.id.editPhoneSignUpManager);
    }

    public void signInManagerClickedInSignUp(View view) {
        String email = editTextEmail.getText().toString();
    }

    public void signUpManagerClicked(View view) {

    }
}
