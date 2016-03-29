package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.ManagerValidation;
import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.SynchronizationService;
import dima.liza.mobile.shenkar.com.otsproject.Validation;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;
import dima.liza.mobile.shenkar.com.otsproject.task.data.Task;




public class ReportTaskActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
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
    TextView textViewProgress,textViewStatus,textViewDeadline;
    TextView texViewEmployeeTask,employeeOfTask,textViewLocationTask;
    Button picture;
    Button reportTask;
    ParseUser currentUser;
    DataAccess dataAccess;
    Task taskToReport;
    String taskIdToReport;
    private boolean taskIsDone = false;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;

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
                ManagerValidation.checkRegisteredEmployee(ReportTaskActivity.this, dataAccess);
            }
        });

        picture = (Button)findViewById(R.id.buttonAddPicture);
        this.imageView = (ImageView)this.findViewById(R.id.CameraImageView);
        reportTask = (Button)findViewById(R.id.reportTask);
        taskCategoryTextView = (TextView) findViewById(R.id.taskCategoryText);
        taskDescriptionTextView = (TextView) findViewById(R.id.taskDescriptionText);
        taskPriorityTextView = (TextView) findViewById(R.id.taskPriorityText);
        taskHeaderTextView = (TextView) findViewById(R.id.textViewHeaderText);
        textViewLocationTask = (TextView) findViewById(R.id.textViewLocationTask);
        radioButtonWaiting = (RadioButton) findViewById(R.id.waitingRadioButton);
        radioButtonAccept = (RadioButton) findViewById(R.id.acceptRadioButton);
        radioButtonReject = (RadioButton) findViewById(R.id.rejectRadioButton);

        radioButtonWaitingInProgress = (RadioButton) findViewById(R.id.waitingProgressRadioButton);
        radioButtonInProgress = (RadioButton) findViewById(R.id.progressRadioButton);
        radioButtonDone  = (RadioButton) findViewById(R.id.doneRadioButton);

        radioGroupStatus = (RadioGroup) findViewById(R.id.radioGroupStatus);
        radioGroupProgress = (RadioGroup) findViewById(R.id.radioGroupProgress);

        textViewDeadline = (TextView) findViewById(R.id.textDeadline);
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
        String  temp = Validation.dateToString(taskToReport.getDeadline());
        textViewDeadline.setText(temp);
        textViewLocationTask.setText(taskToReport.getLocation());
//        if(!taskToReport.isPhotoRequire()){
//            picture.setVisibility(View.INVISIBLE);
//        }
        String status = taskToReport.getStatus();
        if(!currentUser.getBoolean("isManager")) {
            fab.hide();
            switch (status) {
                case "waiting": {
                    radioButtonWaiting.setChecked(true);
                    radioGroupProgress.setVisibility(View.INVISIBLE);
                    textViewStatus.setVisibility(View.VISIBLE);
                    statusOfTask = STATUS_WAITING;
                    break;
                }
                case "accept": {
                    radioButtonAccept.setChecked(true);
                    // radioButtonWaitingInProgress.setChecked(true);
                    radioGroupStatus.setVisibility(View.INVISIBLE);
                    textViewProgress.setVisibility(View.VISIBLE);
                    statusOfTask = STATUS_ACCEPT;
                    break;
                }
                case "reject": {
                    //radioButtonReject.setChecked(true);
                    //  radioGroupStatus.setClickable(false);
                    // radioButtonWaitingInProgress.setChecked(true);
                    reportTask.setText("Go back");
                    taskIsDone = true;
                    radioGroupProgress.setVisibility(View.INVISIBLE);
                    radioGroupStatus.setVisibility(View.INVISIBLE);
                    textViewProgress.setText("You reject the task");
                    textViewProgress.setVisibility(View.VISIBLE);
                    statusOfTask = STATUS_REJECT;
                    break;
                }
                case "inProgress": {
                    radioButtonInProgress.setChecked(true);
                    radioGroupStatus.setVisibility(View.INVISIBLE);
                    textViewProgress.setVisibility(View.VISIBLE);
                    statusOfTask = STATUS_IN_PROGRESS;
                    break;
                }
                case "done": {
                    radioGroupStatus.setVisibility(View.INVISIBLE);
                    radioGroupProgress.setVisibility(View.INVISIBLE);
                    textViewProgress.setText("Task Done");
                    textViewProgress.setVisibility(View.VISIBLE);
                    reportTask.setText("Go back");
                    taskIsDone = true;
                    statusOfTask = STATUS_DONE;
                    break;
                }
                case "cancel": {
                    statusOfTask = STATUS_CANCEL;
                    reportTask.setText("Go back");
                    taskIsDone = true;
                    radioGroupProgress.setVisibility(View.INVISIBLE);
                    radioGroupStatus.setVisibility(View.INVISIBLE);
                    textViewProgress.setText("Task cancel");
                    textViewProgress.setVisibility(View.VISIBLE);
                }
                case "late": {
                    statusOfTask = STATUS_CANCEL;
                    reportTask.setText("Go back");
                    taskIsDone = true;
                    radioGroupProgress.setVisibility(View.INVISIBLE);
                    radioGroupStatus.setVisibility(View.INVISIBLE);
                    textViewProgress.setText("Deadline late");
                    textViewProgress.setVisibility(View.VISIBLE);
                }
                default: {
                    statusOfTask = ERROR;
                    Log.d(TAG, "Status problem of task");
                }
            }
            Log.d(TAG, "statusOfTask:" + statusOfTask);
        }
        else{
            texViewEmployeeTask = (TextView) findViewById(R.id.texViewEmployeeTask);
            texViewEmployeeTask.setVisibility(View.VISIBLE);
            employeeOfTask = (TextView)findViewById(R.id.employeeOfTask);
            employeeOfTask.setVisibility(View.VISIBLE);
            texViewEmployeeTask.setText(taskToReport.getEmployee());
            reportTask.setText("Go back");
            taskIsDone = true;
            radioGroupProgress.setVisibility(View.INVISIBLE);
            radioGroupStatus.setVisibility(View.INVISIBLE);
            textViewProgress.setText("Status:"+taskToReport.getStatus());
            textViewProgress.setVisibility(View.VISIBLE);
        }
        taskDescriptionTextView.setText(taskToReport.getTaskDescription());
        taskHeaderTextView.setText(taskToReport.getTaskHeader());
        taskPriorityTextView.setText(taskToReport.getPriority());
        taskCategoryTextView.setText(taskToReport.getCategory());


        //if there is image for this task show it in the imageView

        ParseQuery<ParseObject> query = ParseQuery.getQuery("images");
        query.whereEqualTo("task_id", taskIdToReport);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) {
                        ;
                    } else {
                        ParseObject fileObject = objects.get(0);
                        ParseFile file = (ParseFile) fileObject.get("imageFile");
                        //get the data of the file
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    //make bytes into usable file
                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    //set the file to the imageView
                                    ImageView pic = (ImageView) findViewById(R.id.CameraImageView);
                                    pic.setImageBitmap(bmp);
                                } else {
                                    Toast.makeText(ReportTaskActivity.this, "Can't load image. Try again", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                    }
                } else {
                    ;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_log_of) {
            ParseUser.logOut();
            this.deleteDatabase("otsProject.db");
            stopService(new Intent(this, SynchronizationService.class));
            Intent intent = new Intent(this,SignInActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.teamTasksDrawer: {
                Intent intent = new Intent(this,ShowTaskManagerActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.editTeamDrawer: {
                Intent intent = new Intent(this,EditTeamActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.taskLocationOption:{
                Intent intent = new Intent(this,LocationsActivity.class);
                startActivity(intent);
                break;
            }
            default:
                Log.d(TAG,"onNavigationItemSelected no such id");
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void onClickAddPicture(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
//
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //save image to parse
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //show the photo you take in the imageView
            imageView.setImageBitmap(photo);
            //prepare image file for parse
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            ParseFile file = new ParseFile("taskPhoto.png", image);
            file.saveInBackground();
            //save in parse
            ParseObject imgupload = new ParseObject("images");
            imgupload.put("employee", currentUser.getEmail());
            imgupload.put("task_id", taskIdToReport);
            imgupload.put("imageFile", file);
            imgupload.saveInBackground();
        }
    }

    public void onClickSaveTaskReport(View view) {
            if(taskIsDone){
                finish();
                return;
            }
            if(statusOfTask == newStatusOfTask){
                Toast.makeText(this,"Task status not change",Toast.LENGTH_LONG).show();
                return;
            }
        String newTaskStatus = "none";

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
                if(statusOfTask == STATUS_ACCEPT){
                    newTaskStatus = "inProgress";
                }
                break;
            }
            case STATUS_DONE:{
                if(statusOfTask == STATUS_IN_PROGRESS || statusOfTask == STATUS_ACCEPT){
                    newTaskStatus = "done";
                }
                break;
            }
            case STATUS_CANCEL:{

                break;
            }
        }
        if(newTaskStatus.equals("none")){
            Toast.makeText(this,"You can't set this status to current task",Toast.LENGTH_LONG).show();
            Log.e(TAG,"Status none");
            return;
        }
        ParseObject task  = new ParseObject("Task");
        task.setObjectId(taskIdToReport);
        task.put("status", newTaskStatus);
        task.put("updateForManager", true);
        final String temp = newTaskStatus;
        task.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
               if(e == null){
                   taskToReport.setStatus(temp);
                   dataAccess.updateTask(taskToReport);
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
