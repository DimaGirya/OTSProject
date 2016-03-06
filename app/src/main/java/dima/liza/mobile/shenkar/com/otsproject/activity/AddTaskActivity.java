package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;

import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccessEmployee;

public class AddTaskActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AdapterView.OnItemSelectedListener {
    private EditText taskDescription;
    private CheckBox checkbox;
    private Button setTime, setDate;
    private Spinner employeeDropDown, categoryDropDown, taskLocationDropDown;
    private ArrayAdapter<String> adapterEmployeeDropDown;
    private static String TAG = "AddTaskActivity";
    private  final static int LOW_PRIORITY = 0;
    private final static int NORMAL_PRIORITY = 1;
    private final static int URGENT_PRIORITY = 2;
    private int priority = LOW_PRIORITY;
    private Date dateOfTask;
    private Date time;
    private Date date;
    private final int TOMORROW = 0;
    private final int TODAY = 1;
    private final int OTHER_DATE = 2;
    private int flagDate = TOMORROW;
    private Boolean requirePhoto = false;
    private DataAccessEmployee dataAccessEmployee;
    private ArrayAdapter<String> adapterCategoryDropDown;
    private ArrayAdapter<String> adapterLocationDropDown;
    private String selectedEmployee,selectedLocation,selectedCategory;
    private ProgressDialog progressDialog;
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
        dataAccessEmployee = DataAccessEmployee.getInstatnce(this);
        String[] employeesName = dataAccessEmployee.getAllRegisteredEmployeesName();
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
        getMenuInflater().inflate(R.menu.add_task, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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

    }

    public void onClickSetDate(View view) {

    }

    public void onClickSubmitTask(View view) {
     // String emplolyee =  employeeDropDown.get
        if(!validationTask()){
            Toast.makeText(this,"Task add validation fail",Toast.LENGTH_LONG).show();
            return;
        }
    //    Toast.makeText(this,"Task add validation ok",Toast.LENGTH_LONG).show();
        ParseObject task = new ParseObject("Task");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sending task to employee");
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        task.put("taskManager", currentUser.getEmail());
        task.put("isDone", false);
        task.put("status",getString(R.string.waiting));
      //  task.put("taskDate",dateOfTask.getTime());
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
        task.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    progressDialog.dismiss();
                    Toast.makeText(AddTaskActivity.this,"Task send to employee",Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(AddTaskActivity.this,"Task not add.Try again later",Toast.LENGTH_LONG).show();
                    Log.d(TAG,"ParseException:",e);
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


        if(flagDate==OTHER_DATE){
            if(time==null && date == null){ //warning
                Toast.makeText(this,"You not select a date or time",Toast.LENGTH_LONG).show();
                return false;
            }
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
                    flagDate = TOMORROW;
                    setTime.setClickable(false);
                    setDate.setClickable(false);
                    Log.i(TAG, "TOMORROW");
                }
                break;
            case R.id.todayRadioButton:
                if (checked) {
                    flagDate = TODAY;
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
}
