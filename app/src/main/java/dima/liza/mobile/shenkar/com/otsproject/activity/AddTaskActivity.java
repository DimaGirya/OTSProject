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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;

public class AddTaskActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AdapterView.OnItemSelectedListener {
    private EditText taskDescription;
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
    private String dateInput;
    private  Date dateTask;
    private int year,month,day,hour,minute;
    private final int TOMORROW = 0;
    private final int TODAY = 1;
    private final int OTHER_DATE = 2;
    private int flagDate = TOMORROW;
    private Boolean requirePhoto = false;
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
        String[] employeesName = dataAccess.getAllRegisteredEmployeesName();
        employeesName[0] = getString(R.string.selectEmployee);
        adapterEmployeeDropDown = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, employeesName);
        adapterEmployeeDropDown.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employeeDropDown.setAdapter(adapterEmployeeDropDown);
        employeeDropDown.setOnItemSelectedListener(this);
        String[] category = {getString(R.string.selectCategory),"General", "Cleaning", "Electricity", "Computers", "Other"};
        adapterCategoryDropDown = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category);
        adapterCategoryDropDown.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryDropDown.setAdapter(adapterCategoryDropDown);
        categoryDropDown.setOnItemSelectedListener(this);
        String[] location = {getString(R.string.selectLocation),"location 1", "location 2", "location 3", "location 4"};
        adapterLocationDropDown = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, location);
        adapterCategoryDropDown.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskLocationDropDown.setAdapter(adapterLocationDropDown);
        taskLocationDropDown.setOnItemSelectedListener(this);
        tomorrowDeadlineSelect();
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
            return true;
        }
        if (id == R.id.action_log_of) {
            ParseUser.logOut();
            this.deleteDatabase("otsProject.db");
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
            case R.id.taskCategoryOption:{
                Toast.makeText(this,"Task category option todo Liza",Toast.LENGTH_LONG).show();
                Log.i(TAG,"taskCategoryOption");
                break;
            }
            case R.id.taskLocationOption:{
                Toast.makeText(this,"Task location option todo Liza",Toast.LENGTH_LONG).show();
                Log.i(TAG,"taskLocationOption");
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
            textTime.setText(hour+":"+minute);
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
                textDate.setText(day+"."+month+"."+year);
                Log.i(TAG, "Date set " + day + " day " + month + " month" + year + " year");
            }
        };
        DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, yearArg, monthArg, dayArg);
        tpd.show();
    }

    public void onClickSubmitTask(View view) {

        GregorianCalendar gc = new GregorianCalendar(year,month,day,hour,minute);
         dateTask =  gc.getTime();
        if(!validationTask()){
            return;
        }
        ParseObject task = new ParseObject("Task");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sending task to employee");
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        task.put("taskManager", currentUser.getEmail());
        task.put("isDone", false);
        task.put("status",getString(R.string.waiting));
        task.put("taskEmployee",selectedEmployee);
        task.put("taskDescription",taskDescription.getText().toString());
        task.put("taskCategory",selectedCategory);
        task.put("taskLocation",selectedLocation);
        task.put("requirePhoto",requirePhoto);
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
        task.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    progressDialog.dismiss();
                    Toast.makeText(AddTaskActivity.this, "Task send to employee", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddTaskActivity.this, "Task not add.Try again later", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "ParseException:", e);
                }
            }
        });



}

    private boolean validationTask() {
        if(taskDescription.getText().toString().isEmpty()){
            Toast.makeText(this,"Task description field is empty",Toast.LENGTH_LONG).show();
            return false;
        }
         if(selectedEmployee.equals(getString(R.string.selectEmployee))){
            Toast.makeText(this,"You not select a employee",Toast.LENGTH_LONG).show();
            return false;
        }
        if(selectedCategory.equals(getString(R.string.selectCategory))){
            Toast.makeText(this,"You not select a category",Toast.LENGTH_LONG).show();
            return false;
        }
        if(selectedLocation.equals(getString(R.string.selectLocation))){
            Toast.makeText(this,"You not select a location",Toast.LENGTH_LONG).show();
            return false;
        }
        //dateTask
        if(dateTask.before(Calendar.getInstance().getTime())){
            Toast.makeText(this,"You can't select date from past",Toast.LENGTH_LONG).show();
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
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    textTime.setText("17:00");
                    textDate.setText(day+"."+month+"."+year);

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
        int year = gc.get(Calendar.YEAR);
        int month = gc.get(Calendar.MONTH);
        int day = gc.get(Calendar.DAY_OF_MONTH);
        textTime.setText("17:00");
        textDate.setText(day+"."+month+"."+year);
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
