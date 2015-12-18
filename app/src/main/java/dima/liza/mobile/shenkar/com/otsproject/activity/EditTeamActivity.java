package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    public void onClickSaveTeamName(View view) {

    }

    public void onClickInviteMembers(View view) {

    }


    public void onClickDeleteMembers(View view) {

    }

    public void onCLickEditTeamDone(View view) {

    }

    public void onClickAddTask(View view) {
    }
}
