package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import dima.liza.mobile.shenkar.com.otsproject.R;

public class EditTeamActivity extends AppCompatActivity {
    EditText teamName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team);
       //  FloatingActionButton floatingActionButton = new FloatingActionButton(this);
        teamName = (EditText) findViewById(R.id.editTeamNameTextField);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
     //   getMenuInflater().inflate(R.menu.);
        return super.onCreateOptionsMenu(menu);

    }

    public void onClickSaveTeamName(View view) {

    }

    public void onClickInviteMembers(View view) {
        Intent intent = new Intent(this, AddEmployeeActivity.class);
        startActivity(intent);
    }


    public void onClickDeleteMembers(View view) {

    }

    public void onCLickEditTeamDone(View view) {

    }

    public void onClickAddTask(View view) {

    }
}
