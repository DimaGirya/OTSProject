package dima.liza.mobile.shenkar.com.otsproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SignInActivityManager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_activity_manager);
    }

    public void onClickGotoSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivityManager.class);
        startActivity(intent);
    }
}
