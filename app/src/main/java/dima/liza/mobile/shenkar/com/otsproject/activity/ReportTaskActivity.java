package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;

import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;
import dima.liza.mobile.shenkar.com.otsproject.task.data.Task;

public class ReportTaskActivity extends AppCompatActivity {
    private static final String TAG = "ReportTaskActivity" ;
    private static final int STATUS_WAITING = 1;
    private static final int STATUS_ACCEPT = 2;
    private static final int STATUS_REJECT = 3;
    private static final int STATUS_IN_PROGRESS = 4;
    private static final int STATUS_DONE = 5;
    private static final int STATUS_CANCEL = 6;
    private static final int ERROR = 0;
    private int statusOfTask;
    private int newStatusOfTask;

    RadioButton radioButtonWaiting,radioButtonAccept,radioButtonReject;
    RadioButton radioButtonWaitingInProgress,radioButtonInProgress,radioButtonDone;
    RadioGroup radioGroupStatus,radioGroupProgress;
    TextView taskCategoryTextView, taskDescriptionTextView, taskHeaderTextView ,taskPriorityTextView;
    TextView textViewProgress,textViewStatus;
    Button picture;
    ParseUser currentUser;
    DataAccess dataAccess;
    Task taskToReport;
    String taskIdToReport;


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
        picture = (Button)findViewById(R.id.buttonAddPicture);
        taskCategoryTextView = (TextView) findViewById(R.id.taskCategoryText);
        taskDescriptionTextView = (TextView) findViewById(R.id.taskDescriptionText);
        taskPriorityTextView = (TextView) findViewById(R.id.taskPriorityText);
        taskHeaderTextView = (TextView) findViewById(R.id.textViewHeaderText);

        radioButtonWaiting = (RadioButton) findViewById(R.id.waitingRadioButton);
        radioButtonAccept = (RadioButton) findViewById(R.id.acceptRadioButton);
        radioButtonReject = (RadioButton) findViewById(R.id.rejectRadioButton);

        radioButtonWaitingInProgress = (RadioButton) findViewById(R.id.waitingProgressRadioButton);
        radioButtonInProgress = (RadioButton) findViewById(R.id.progressRadioButton);
        radioButtonDone  = (RadioButton) findViewById(R.id.doneRadioButton);

        radioGroupStatus = (RadioGroup) findViewById(R.id.radioGroupStatus);
        radioGroupProgress = (RadioGroup) findViewById(R.id.radioGroupProgress);
        textViewProgress = (TextView)findViewById(R.id.textViewProgress);
        textViewProgress.setVisibility(View.INVISIBLE);
        textViewStatus = (TextView)findViewById(R.id.textViewStatus);
        textViewStatus.setVisibility(View.INVISIBLE);
        currentUser =  ParseUser.getCurrentUser();
        Intent intent = getIntent();
        taskIdToReport = intent.getStringExtra("taskId");
        if(taskIdToReport==null){
            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
            Log.d(TAG, "TaskID is null");
            finish();
        }
        dataAccess = DataAccess.getInstatnce(this);
        taskToReport = dataAccess.getTaskById(taskIdToReport);
        picture.setClickable(taskToReport.isPhotoRequire());
        String status = taskToReport.getStatus();

        switch (status){
            case "waiting":{
                radioButtonWaiting.setChecked(true);
                radioGroupProgress.setVisibility(View.INVISIBLE);
                textViewStatus.setVisibility(View.VISIBLE);
                statusOfTask = STATUS_WAITING;
                break;
            }
            case "accept":{
                radioButtonAccept.setChecked(true);
               // radioButtonWaitingInProgress.setChecked(true);
                radioGroupStatus.setVisibility(View.INVISIBLE);
                textViewProgress.setVisibility(View.VISIBLE);
                statusOfTask = STATUS_ACCEPT;
                break;
            }
            case "reject":{
                //radioButtonReject.setChecked(true);
              //  radioGroupStatus.setClickable(false);
               // radioButtonWaitingInProgress.setChecked(true);
                radioGroupProgress.setVisibility(View.INVISIBLE);
                radioGroupStatus.setVisibility(View.INVISIBLE);
                textViewProgress.setText("You reject the task");
                textViewProgress.setVisibility(View.VISIBLE);
                statusOfTask = STATUS_REJECT;
                break;
            }
            case "inProgress":{
                radioButtonInProgress.setChecked(true);
                radioGroupStatus.setVisibility(View.INVISIBLE);
                textViewProgress.setVisibility(View.VISIBLE);
                statusOfTask = STATUS_IN_PROGRESS;
                break;
            }
            case "done":{
                radioGroupStatus.setVisibility(View.INVISIBLE);
                radioGroupProgress.setVisibility(View.INVISIBLE);
                textViewProgress.setText("Task Done");
                textViewProgress.setVisibility(View.VISIBLE);
                statusOfTask = STATUS_DONE;
                break;
            }
            case "cancel":{
                statusOfTask = STATUS_CANCEL;
                radioGroupProgress.setVisibility(View.INVISIBLE);
                radioGroupStatus.setVisibility(View.INVISIBLE);
                textViewProgress.setText("Task cancel");
                textViewProgress.setVisibility(View.VISIBLE);

            }
            default:{
                statusOfTask = ERROR;
                Log.d(TAG,"Status problem of task");
            }

        }


        taskDescriptionTextView.setText(taskToReport.getTaskDescription());
        taskHeaderTextView.setText(taskToReport.getTaskHeader());
        taskPriorityTextView.setText("todo");   // replace to priority
        taskCategoryTextView.setText(taskToReport.getCategory());
    }




    public void onClickAddPicture(View view) {
        Log.d(TAG,"onClickAddPicture todo");
    }

    public void onClickSaveTaskReport(View view) {
            if(statusOfTask == newStatusOfTask){
                Toast.makeText(this,"Task status not change",Toast.LENGTH_LONG).show();
                return;
            }
        String newTaskStatus = "none";
        /*
          private static final int STATUS_WAITING = 1;
    private static final int STATUS_ACCEPT = 2;
    private static final int STATUS_REJECT = 3;
    private static final int STATUS_IN_PROGRESS = 4;
    private static final int STATUS_DONE = 5;
    private static final int STATUS_CANCEL = 6;
         */
        switch(newStatusOfTask){
            case STATUS_WAITING:{

                break;
            }
            case STATUS_ACCEPT:{
                if(statusOfTask == STATUS_WAITING){
                    newTaskStatus = "accept";
                }
                break;
            }
            case STATUS_REJECT:{
                if(statusOfTask == STATUS_WAITING){
                    newTaskStatus = "reject";
                }
                break;
            }
            case STATUS_IN_PROGRESS:{
                if(statusOfTask == STATUS_WAITING){
                    newTaskStatus = "inProgress";
                }
                break;
            }
            case STATUS_DONE:{
                if(statusOfTask == STATUS_IN_PROGRESS){
                    newTaskStatus = "done";
                }
                break;
            }
            case STATUS_CANCEL:{

                break;
            }
        }
        if(newTaskStatus.equals("none")){
            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
            Log.e(TAG,"Status none");
            return;
        }
        ParseObject task  = new ParseObject("Task");
        task.setObjectId(taskIdToReport);
        task.put("status", newTaskStatus);
        task.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
               if(e == null){
                   Toast.makeText(ReportTaskActivity.this,"Status save",Toast.LENGTH_LONG).show();
                   finish();
               }else{
                   Toast.makeText(ReportTaskActivity.this,"Connection error.Try again letter",Toast.LENGTH_LONG).show();
                   Log.e(TAG,"ParseException:",e);
               }
            }
        });
    }


    public void onClickRadioGroupStatus(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.waitingRadioButton:
                if (checked) {
                    newStatusOfTask = STATUS_WAITING;
                    Log.d(TAG,"onClickRadioGroupStatus STATUS_WAITING");
                }
                break;
            case R.id.acceptRadioButton:
                if (checked) {
                    newStatusOfTask = STATUS_ACCEPT;
                    Log.d(TAG,"onClickRadioGroupStatus STATUS_ACCEPT");
                }
                break;
            case R.id.rejectRadioButton:
                if (checked) {
                    newStatusOfTask = STATUS_REJECT;
                    Log.d(TAG,"onClickRadioGroupStatus STATUS_REJECT");
                }
                break;
            default: {
                Log.d(TAG,"onClickRadioGroupStatus error");
            }
        }

    }

    /*
            radioButtonWaitingInProgress = (RadioButton) findViewById(R.id.waitingProgressRadioButton);
        radioButtonInProgress = (RadioButton) findViewById(R.id.progressRadioButton);
        radioButtonDone  = (RadioButton) findViewById(R.id.doneRadioButton);
     */

    public void onClickRadioGroupProgress(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.waitingProgressRadioButton:
                if (checked) {
                    newStatusOfTask = STATUS_WAITING;
                    Log.d(TAG,"onClickRadioGroupProgress STATUS_WAITING");
                }
                break;
            case R.id.progressRadioButton:
                if (checked) {
                    newStatusOfTask = STATUS_IN_PROGRESS;
                    Log.d(TAG,"onClickRadioGroupProgress STATUS_IN_PROGRESS");
                }
                break;
            case R.id.doneRadioButton:
                if (checked) {
                    newStatusOfTask = STATUS_DONE;
                    Log.d(TAG,"onClickRadioGroupProgress STATUS_DONE");
                }
                break;
            default: {
                Log.d(TAG,"onClickRadioGroupProgress error");
            }
        }
    }
}
