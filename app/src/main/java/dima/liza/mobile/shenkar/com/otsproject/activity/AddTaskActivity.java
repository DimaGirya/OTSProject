package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.SynchronizationService;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;
import dima.liza.mobile.shenkar.com.otsproject.task.data.Task;

public class AddTaskActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AdapterView.OnItemSelectedListener {
    private static final int HEADER_TASK_MAX_LENGTH = 20 ;
    private EditText taskDescription,taskHeader;
    private TextView textTime,textDate;
    private CheckBox checkbox;
    private Button setTime, setDate;
    private Spinner employeeDropDown, categoryDropDown, taskLocationDropDown;
    private ArrayAdapter<String> adapterEmployeeDropDown;
    private static String TAG = "AddTaskActivity";
    private  final static int LOW_PRIORITY = 0;
    private final static int NORMAL_PRIORITY = 1;
    private final static int URGENT_PRIORITY = 2;
    private int priority = LOW_PRIORITY;
    private  Date dateTask;
    private int year,month,day,hour,minute;
    private final int TOMORROW = 0;
    private final int TODAY = 1;
    private final int OTHER_DATE = 2;
    private int flagDate = TOMORROW;
    private Boolean requirePhoto = false;
    private Boolean updateTask;
    private DataAccess dataAccess;
    private ArrayAdapter<String> adapterCategoryDropDown;
    private ArrayAdapter<String> adapterLocationDropDown;
    private String selectedEmployee,selectedLocation,selectedCategory;
    private ProgressDialog progressDialog;
    private TextView userName,userEmail;
    ParseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        currentUser = ParseUser.getCurrentUser();
        checkbox = (CheckBox) findViewById(R.id.checkBoxRequirePhoto);
        taskDescription = (EditText) findViewById(R.id.editTextTaskDescription);
        taskHeader = (EditText)findViewById(R.id.editTextTaskHeader);
        employeeDropDown = (Spinner) findViewById(R.id.employeesDropDown);
        categoryDropDown = (Spinner) findViewById(R.id.categoryDropDown);
        taskLocationDropDown = (Spinner) findViewById(R.id.taskLocationDropDown);
        setTime = (Button) findViewById(R.id.buttonSetTime);
        setTime.setClickable(false);
        setDate = (Button) findViewById(R.id.buttonSetDate);
        setDate.setClickable(false);
        textTime = (TextView)findViewById(R.id.timeTask);
        textDate = (TextView)findViewById(R.id.dateTask);
        dataAccess = DataAccess.getInstatnce(this);

        String[] category = {getString(R.string.selectCategory),getString(R.string.general), getString(R.string.cleaning), getString(R.string.electricity), getString(R.string.computers), getString(R.string.other)};
        String[] location;
        location = dataAccess.getLocations();
        if(location.length==1) {
            location = new String[2];
            location[0] = getString(R.string.selectLocation);
            location[1] = getString(R.string.defaultLocation);//default value
        }
        String[] employeesName;
        Intent intent = getIntent();
        updateTask = intent.getBooleanExtra("editTask",false);
        if(updateTask){   //edit task mode
            String taskId = intent.getStringExtra("taskId");
            Task editTask = dataAccess.getTaskById(taskId);
            if(editTask==null){
                Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
                Log.d(TAG,"taskId is null");
                finish();
            }
            taskDescription.setText(editTask.getTaskDescription());
            taskHeader.setText(editTask.getTaskHeader());
            checkbox.setChecked(editTask.isPhotoRequire());
             category[0] = editTask.getCategory();
            location[0] = editTask.getLocation();
            employeesName = new String[1];
            employeesName[0] = editTask.getEmployee();
            RadioGroup radioGroupTime = (RadioGroup)findViewById(R.id.radioGroupTime);
            radioGroupTime.check(R.id.otherDateTimeRadioButton);
            flagDate = OTHER_DATE;
            Date taskDate = editTask.getDeadline();
            Log.d(TAG,"taskDate:"+taskDate.toString());
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(taskDate);
            day = calendar.get(calendar.DAY_OF_MONTH);
            hour = calendar.get(calendar.HOUR_OF_DAY);
            year = calendar.get(calendar.YEAR);
            textDate.setText(day+getString(R.string.dot)+month+getString(R.string.dot)+year);
            hour = calendar.get(calendar.HOUR_OF_DAY);
            minute = calendar.get(calendar.MINUTE);
            textTime.setText(hour+getString(R.string.doubleDot)+minute);

        }else{
            employeesName = dataAccess.getAllRegisteredEmployeesName();
            employeesName[0] = getString(R.string.selectEmployee);
            tomorrowDeadlineSelect();

        }
        adapterCategoryDropDown = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category);
        adapterCategoryDropDown.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryDropDown.setAdapter(adapterCategoryDropDown);
        categoryDropDown.setOnItemSelectedListener(this);


        adapterEmployeeDropDown = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, employeesName);
        adapterEmployeeDropDown.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employeeDropDown.setAdapter(adapterEmployeeDropDown);
        employeeDropDown.setOnItemSelectedListener(this);

        adapterLocationDropDown = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, location);
        adapterCategoryDropDown.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskLocationDropDown.setAdapter(adapterLocationDropDown);
        taskLocationDropDown.setOnItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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




    public void onClickRadioGroupPriority(View view) {
        Log.i(TAG, "onClickRadioGroupPriority");
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.lowPriorityRadioButton:
                if (checked) {
                    priority = LOW_PRIORITY;
                    Log.i(TAG, "LOW_PRIORITY");
                }
                break;
            case R.id.normalPriorityRadioButton:
                if (checked) {
                    priority = NORMAL_PRIORITY;
                    Log.i(TAG, "NORMAL_PRIORITY");
                }
                break;
            case R.id.urgentPriorityRadioButton:
                if (checked) {
                    priority = URGENT_PRIORITY;
                    Log.i(TAG, "URGENT_PRIORITY");
                }
                break;
            default: {
                Log.i(TAG, "Something's wrong in onClick RadioGroup Priority");
            }
        }
    }

    public void onClickSetTime(View view) {
        TimePickerDialog tpd = new TimePickerDialog(this, myCallBack,0, 0, true);
        DialogFragment dialogFragment = new DialogFragment();
        tpd.show();
    }

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
            hour = hourOfDay;
            minute = minuteOfHour;
            textTime.setText(hour+getString(R.string.doubleDot)+minute);
            Log.i(TAG, "Time set " + hour + " hours " + minute + " minutes");
        }
    };

    public void onClickSetDate(View view) {
        Calendar calendar = Calendar.getInstance();
        int yearArg = calendar.get(Calendar.YEAR);
        int monthArg = calendar.get(Calendar.MONTH);
        int dayArg = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int yearInput, int monthInput, int dayInput) {
                year = yearInput;
                month = monthInput;
                day = dayInput;
                textDate.setText(day+getString(R.string.dot)+month+getString(R.string.dot)+year);
                Log.i(TAG, "Date set " + day + " day " + month + " month" + year + " year");
            }
        };
        DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, yearArg, monthArg, dayArg);
        tpd.show();
    }

    public void onClickSubmitTask(View view) {

        GregorianCalendar gc = new GregorianCalendar(year,month,day,hour,minute);
        gc.setTimeZone(TimeZone.getDefault());
         dateTask =  gc.getTime();
        if(!validationTask()){
            return;
        }
        final ParseObject task = new ParseObject("Task");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.sendTaskToEmployee));
        progressDialog.setMessage(getString(R.string.pleaseWait));
        progressDialog.show();
        task.put("taskManager", currentUser.getEmail());
        task.put("isDone", false);
        task.put("status","waiting");
        task.put("taskHeader",taskHeader.getText().toString());
        task.put("taskEmployee",selectedEmployee);
        task.put("taskDescription",taskDescription.getText().toString());
        task.put("taskCategory",selectedCategory);
        task.put("taskLocation",selectedLocation);
        task.put("requirePhoto",requirePhoto);
        task.put("updateForEmployee",true);
        task.put("photoUploaded",false);
        switch (priority){
            case LOW_PRIORITY: {
                task.put("priority","low");
                break;
            }
            case NORMAL_PRIORITY: {
                task.put("priority","normal");
                break;
            }
            case URGENT_PRIORITY: {
                task.put("priority","urgent");
                break;
            }
            default:
                task.put("priority","not_set");
                Log.d(TAG,"priority not_set");
        }

        task.put("taskDate",dateTask);
        Log.d(TAG, "Time:" + gc.getTime().toString());
        if(updateTask){
            Intent intent = getIntent();
            String taskId = intent.getStringExtra("taskId");
            task.setObjectId(taskId);
        }
        task.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG,"New object id is a:"+task.getObjectId());
                   String taskDescription = task.getString("taskDescription");
                    String taskHeader = task.getString("taskHeader");
                    String employee = task.getString("taskEmployee");
                    Date  deadline = task.getDate("taskDate");
                    SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
                    String deadlineStr = dateFormat.format(deadline);
                    String   status = task.getString("status");
                    String category = task.getString("taskCategory");
                    String location = task.getString("taskLocation");
                    String parseId = task.getObjectId();
                    Boolean photoRequire = task.getBoolean("photoRequire");
                    String priority = task.getString("priority");
                    Task  newTask = new Task (taskHeader,taskDescription,employee,deadline,priority,status,category,location,photoRequire,parseId,deadlineStr);
                    dataAccess.insertTask(newTask);
                    progressDialog.dismiss();
                    Toast.makeText(AddTaskActivity.this, R.string.taskSendToEmployee, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddTaskActivity.this, R.string.taskNotAdd, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "ParseException:", e);
                }
            }
        });
}

    private boolean validationTask() {
        if(taskHeader.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.headerEmpty,Toast.LENGTH_LONG).show();
            return false;
        }
        if(taskHeader.getText().toString().length()>HEADER_TASK_MAX_LENGTH){
            Toast.makeText(this,getString(R.string.taskHeaderMaximum)+HEADER_TASK_MAX_LENGTH,Toast.LENGTH_LONG).show();
            return false;
        }
        if(taskDescription.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.taskDescriptionEmpty,Toast.LENGTH_LONG).show();
            return false;
        }
         if(selectedEmployee.equals(getString(R.string.selectEmployee))){
            Toast.makeText(this, R.string.notSelectEmployee,Toast.LENGTH_LONG).show();
            return false;
        }
        if(selectedCategory.equals(getString(R.string.selectCategory))){
            Toast.makeText(this, R.string.notSelectCategory,Toast.LENGTH_LONG).show();
            return false;
        }
        if(selectedLocation.equals(getString(R.string.selectLocation))){
            Toast.makeText(this, R.string.notSelectLocation,Toast.LENGTH_LONG).show();
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date now = cal.getTime();
        Log.d(TAG,"Time now:"+now.toString());
        Log.d(TAG,"Time deadline task:"+dateTask.toString());
        if(dateTask.before(now)){
            Toast.makeText(this, R.string.dateFromPast,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    public void onClickRequirePhoto(View view) {
        requirePhoto = checkbox.isChecked();
        Log.d(TAG, "requirePhoto:" + requirePhoto);
    }

    public void onClickRadioGroupTimeDate(View view) {
        Log.i(TAG, "onClickRadioGroupTimeDate");
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.tomorrowRadioButton:
                if (checked) {
                    tomorrowDeadlineSelect();
                }
                break;
            case R.id.todayRadioButton:
                if (checked) {
                    flagDate = TODAY;
                    Calendar calendar = Calendar.getInstance();
                     year = calendar.get(Calendar.YEAR);
                     month = calendar.get(Calendar.MONTH);
                     day = calendar.get(Calendar.DAY_OF_MONTH);
                    textTime.setText("17:00");
                    textDate.setText(day+getString(R.string.dot)+(month+1)+getString(R.string.dot)+year);

                    setTime.setClickable(false);
                    setDate.setClickable(false);
                    Log.i(TAG, "TODAY");
                }
                break;
            case R.id.otherDateTimeRadioButton:
                if (checked) {
                    flagDate = OTHER_DATE;
                    setTime.setClickable(true);
                    setDate.setClickable(true);
                    Log.i(TAG, "OTHER_DATE");
                }
                break;
            default: {
                Log.i(TAG, "Something's wrong in onClick onClickRadioGroupTimeDate");
            }
        }
    }

    private void tomorrowDeadlineSelect() {
        flagDate = TOMORROW;
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, 1);
         year = gc.get(Calendar.YEAR);
         month = gc.get(Calendar.MONTH);
         day = gc.get(Calendar.DAY_OF_MONTH);
        hour = 17;
        minute = 0;
        textTime.setText("17:00");
        textDate.setText(day+getString(R.string.dot)+(month+1)+getString(R.string.dot)+year);
        //textTime;
        setTime.setClickable(false);
        setDate.setClickable(false);
        Log.i(TAG, "TOMORROW");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==employeeDropDown.getId()) {
            selectedEmployee = (String) parent.getItemAtPosition(position);
            Log.d(TAG, "employeeDropDown:" + selectedEmployee);
        }
        else if(parent.getId()==taskLocationDropDown.getId()){
             selectedLocation = (String) parent.getItemAtPosition(position);
            Log.d(TAG, "taskLocationDropDown:" + selectedLocation);
        }
        else if(parent.getId()==categoryDropDown.getId()){
             selectedCategory = (String) parent.getItemAtPosition(position);
            Log.d(TAG, "taskLocationDropDown:" + selectedCategory);
        }
        else{
            Log.d(TAG,"Something's wrong in onItemSelected");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(TAG,"Something's wrong.onNothingSelected called");
    }
    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        userName = (TextView) findViewById(R.id.userNameNav);
        userEmail = (TextView) findViewById(R.id.userEmailNav);
        userName.setText(currentUser.getUsername());
        userEmail.setText(currentUser.getEmail());
        return super.onPreparePanel(featureId, view, menu);
    }

}
