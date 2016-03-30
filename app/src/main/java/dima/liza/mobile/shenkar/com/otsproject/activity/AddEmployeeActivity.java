package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.AboutActivity;
import dima.liza.mobile.shenkar.com.otsproject.ManagerValidation;
import dima.liza.mobile.shenkar.com.otsproject.NotificationControl;
import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.SynchronizationService;
import dima.liza.mobile.shenkar.com.otsproject.Validation;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.AdapterEmployeeToAdd;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.Employee;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.EmployeeToAdd;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;

public class AddEmployeeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ProgressDialog progressDialog;
    EditText editTextEmail,editTextPhone;
    ListView listView;
    List<EmployeeToAdd> listEmployeeToAdd;
    ListAdapter adapter;
    DataAccess dataAccess;
    int numOfNewEmployee;
    int numOfAddNewEmployee;
    private static String TAG  = "AddEmployeeActivity";
    TextView userName,userEmail;
    EditText messageToEmployee;
    ParseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee_activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dataAccess = DataAccess.getInstatnce(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerValidation.checkRegisteredEmployee(AddEmployeeActivity.this, dataAccess);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
         currentUser = ParseUser.getCurrentUser();
        messageToEmployee = (EditText) findViewById(R.id.messageToEmployee);
        editTextEmail = (EditText) findViewById(R.id.addEmployeeEmail);
        editTextPhone = (EditText) findViewById(R.id.addEmployeePhone);
        listEmployeeToAdd = new ArrayList();
        adapter =  new AdapterEmployeeToAdd(this,listEmployeeToAdd);
        listView = (ListView) findViewById(R.id.listViewAddEmployee);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        userName = (TextView) findViewById(R.id.userNameNav);
        userEmail = (TextView) findViewById(R.id.userEmailNav);
        userName.setText(currentUser.getUsername());
        userEmail.setText(currentUser.getEmail());
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        if(ParseUser.getCurrentUser()==null){
            finish();
        }
        super.onResume();
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
        if(id == R.id.action_about){
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
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

    @Override
    public void onBackPressed() {
        if(listEmployeeToAdd.size()>0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.confrim));
            builder.setMessage(getString(R.string.areYouSure) + listEmployeeToAdd.size() + getString(R.string.employeeNotAdd));

            builder.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    AddEmployeeActivity.super.onBackPressed();
                    dialog.dismiss();
                }

            });

            builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
        else{
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }




    public void onClickAddEmployeeSubmit(View view) {
        if(listEmployeeToAdd.size()==0){
            Toast.makeText(this, R.string.noEmployessToAdd,Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.addingEmployee));
        progressDialog.setMessage(getString(R.string.pleaseWait));
        progressDialog.show();
        ParseUser currentUser = ParseUser.getCurrentUser();
        EmployeeToAdd employeeToAdd;
        numOfNewEmployee = listEmployeeToAdd.size();
        numOfAddNewEmployee = 0;
        for (int i = 0; i < listEmployeeToAdd.size(); i++) {
            ParseObject newEmployee = new ParseObject("newEmployee");
            employeeToAdd = listEmployeeToAdd.get(i);
            newEmployee.put("email", employeeToAdd.getEmail());
            newEmployee.put("numberPhone", employeeToAdd.getPhone());
            newEmployee.put("manager", currentUser.getEmail());
            newEmployee.put("isManager", false);
            newEmployee.put("statusEmployeeChange",true);
            newEmployee.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {    // SIGN UP DONE
                        numOfAddNewEmployee++;
                        // todo send email to user
                        Toast.makeText(AddEmployeeActivity.this, R.string.addNewEmployeeDone, Toast.LENGTH_LONG).show();
                        if (numOfAddNewEmployee == numOfNewEmployee) {
                            NotificationControl.notificationNow(getString(R.string.addEmployeeDone), numOfNewEmployee + getString(R.string.added),
                                    R.drawable.ic_launcher, 1, AddEmployeeActivity.this, null);
                            DataAccess dataAccess = DataAccess.getInstatnce(AddEmployeeActivity.this);
                            Employee employee;
                            for (int i = 0; i < listEmployeeToAdd.size(); i++) {
                                employee = new Employee(listEmployeeToAdd.get(i));
                                dataAccess.insertEmployee(employee);
                            }
                            Toast.makeText(AddEmployeeActivity.this, R.string.addAllNewEmployeeDone, Toast.LENGTH_LONG).show();
                            try {
                                progressDialog.dismiss();
                            }
                            catch (Exception e1){
                                Log.d(TAG,"Exception",e1);
                            }
                            finish();
                        }

                    } else {
                        Toast.makeText(AddEmployeeActivity.this, R.string.addEmployeeFail, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "ParseException:", e);
                        NotificationControl.notificationNow(getString(R.string.addEmployeeFailNotification), "Oops! Try again later.Error is a:" + e.getMessage(),
                                R.drawable.ic_launcher, 2, AddEmployeeActivity.this, null);
                        progressDialog.dismiss();
                    }
                }
            });

        }


        String  emails[] = new String [listEmployeeToAdd.size()];
        for(int i = 0;i < listEmployeeToAdd.size();i++) {
            emails[i] = listEmployeeToAdd.get(i).getEmail();
        }
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Invitation to Join OTS team");
        emailIntent.putExtra(Intent.EXTRA_TEXT,messageToEmployee.getText().toString());
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AddEmployeeActivity.this, R.string.noEmailClient, Toast.LENGTH_SHORT).show();
        }




    }

    public void onClickAddToList(View view) {
        String emailToAdd = editTextEmail.getText().toString();
        String phoneToAdd =  editTextPhone.getText().toString();

        if(!Validation.emailValidation(emailToAdd, this)){
            return;
        }
        if(!Validation.phoneNumberValidation(phoneToAdd,this)){
            return;
        }

        EmployeeToAdd newEmployee = new EmployeeToAdd(emailToAdd,phoneToAdd);
        listEmployeeToAdd.add(newEmployee);



        editTextEmail.setText("");
        editTextPhone.setText("");
    }



}
