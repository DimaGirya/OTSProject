package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.ManagerValidation;
import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.SynchronizationService;
import dima.liza.mobile.shenkar.com.otsproject.UpdateData;
import dima.liza.mobile.shenkar.com.otsproject.Validation;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.AdapterEmployee;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.Employee;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;

public class EditTeamActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    EditText teamName;
    ListView listView;
    List<Employee> listEmployee;
    ListAdapter adapter;
    ParseUser currentUser;
    SharedPreferences teamNameSharedPreferences;
    SharedPreferences lastUpdateData;
    String teamNameStr;
    ProgressDialog progressDialog;
    DataAccess dataAccess;
    TextView userName,userEmail;
    final String TAG = "EditTeamActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team_activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dataAccess = DataAccess.getInstatnce(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ManagerValidation.checkRegisteredEmployee(EditTeamActivity.this, dataAccess);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        teamName = (EditText) findViewById(R.id.editTeamNameTextField);
        currentUser = ParseUser.getCurrentUser();
        teamNameSharedPreferences = getSharedPreferences("Team", MODE_PRIVATE);
        String savedText = teamNameSharedPreferences.getString("TeamName","");
        teamName.setText(savedText);


        if(!Validation.isOnline(this)){
            if(Validation.doesDatabaseExist(this, "otsProject.db")) {
                Toast.makeText(EditTeamActivity.this, R.string.conectionProblem,Toast.LENGTH_LONG).show();
            }
            else{
                return;
            }
        }else{
           UpdateData.getInstance().updateEmployeeList(this);
        }
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
    protected void onResume() {
        listEmployee = dataAccess.getAllEmployee();
        adapter = new AdapterEmployee(this, listEmployee);
        listView = (ListView) findViewById(R.id.listViewTeamMembers);
        listView.setAdapter(adapter);
        super.onResume();

    }
    private String getLastUpdateEmployeeList() {
        if(!Validation.doesDatabaseExist(this, "otsProject.db")){
                return null;
            }
        lastUpdateData = getSharedPreferences("DateDataUpdate",MODE_PRIVATE);
        String lastUpdate = lastUpdateData.getString("lastEmployeeDateUpdate","");
        if(lastUpdate.equals("")){
            return null;
        }
        return lastUpdate;
    }

    public void onClickSaveTeamName(View view) {
        teamNameStr = teamName.getText().toString();
        if(teamNameStr.isEmpty()){
            Toast.makeText(this, R.string.enterTeamName, Toast.LENGTH_LONG).show();
        }
        progressDialog = new ProgressDialog(this);
        Log.d(TAG, "progressDialog start");
        progressDialog.setTitle(getString(R.string.changeYourTeamName));
        progressDialog.setMessage(getString(R.string.pleaseWait));
        progressDialog.show();
        ParseQuery<ParseObject> query =  ParseQuery.getQuery("Team");
        query.whereEqualTo("Manager",currentUser.getEmail());
        Log.d(TAG, "findInBackground start");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.d(TAG, "findInBackground done");
                if (e == null) {
                    if (objects.size() != 1) {
                        Log.d(TAG, "Object size" + objects.size());
                    } else {
                        objects.get(0).put("TeamName", teamNameStr);
                        Log.d(TAG, "saveInBackground start");
                        objects.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d(TAG, "saveInBackground done");
                                    teamNameSharedPreferences = getSharedPreferences("Team", MODE_PRIVATE);
                                    SharedPreferences.Editor ed = teamNameSharedPreferences.edit();
                                    ed.putString("TeamName", teamNameStr);
                                    ed.commit();
                                    Log.d(TAG, "SharedPreferences done");
                                    progressDialog.dismiss();
                                } else {
                                    Toast.makeText(EditTeamActivity.this, R.string.conectionProblem,Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "saveInBackground exception", e);
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(EditTeamActivity.this,R.string.conectionProblem,Toast.LENGTH_LONG).show();
                    Log.d(TAG, "findInBackground exception", e);
                }
            }
        });
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
            stopService(new Intent(this, SynchronizationService.class));
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
    public void onClickInviteMembers(View view) {
        Intent intent = new Intent(this, AddEmployeeActivity.class);
        startActivity(intent);
    }



    public void onCLickEditTeamDone(View view) {
       Intent intent = new Intent(EditTeamActivity.this,ShowTaskManagerActivity.class);
       startActivity(intent);
    }

}


