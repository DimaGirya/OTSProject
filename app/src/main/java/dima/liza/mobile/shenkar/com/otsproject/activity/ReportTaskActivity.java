package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;

public class ReportTaskActivity extends AppCompatActivity {
    TextView taskCatTxt, taskDescTxt, taskPTxt;
    RadioButton taskStatusW, taskStatusA, taskStatusR, taskPriorityW, taskPriorityI, taskPriorityD;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();
        taskCatTxt = (TextView) findViewById(R.id.taskCatText);
        taskDescTxt = (TextView) findViewById(R.id.taskDescText);
        taskPTxt = (TextView) findViewById(R.id.taskPriorityText);
        taskStatusW = (RadioButton) findViewById(R.id.waitingRB);
        taskStatusA = (RadioButton) findViewById(R.id.acceptRB);
        taskStatusR = (RadioButton) findViewById(R.id.rejectRB);
        taskPriorityW = (RadioButton) findViewById(R.id.taskPwaitingRB);
        taskPriorityI = (RadioButton) findViewById(R.id.inPrRB);
        taskPriorityD = (RadioButton) findViewById(R.id.doneRB);
        currentUser =  ParseUser.getCurrentUser();
        DataAccess dataAccess;
    }

}
