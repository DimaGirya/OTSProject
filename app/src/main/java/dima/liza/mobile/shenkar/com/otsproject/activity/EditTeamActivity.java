package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.AdapterEmployee;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.Employee;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccessEmployee;

public class EditTeamActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    EditText teamName;
    ListView listView;
    List<Employee> listEmployee;
    ListAdapter adapter;
    ParseUser currentUser;
    SharedPreferences teamNameSharedPreferences;
    String teamNameStr;
    ProgressDialog progressDialog;
    DataAccessEmployee dataAccessEmployee;
    final String TAG = "EditTeamActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team_activity_nav);
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
        dataAccessEmployee = DataAccessEmployee.getInstatnce(this);
        //todo check logic
        listEmployee = dataAccessEmployee.getAllEmployee();
        adapter =  new AdapterEmployee(this,listEmployee);
        listView = (ListView) findViewById(R.id.listViewTeamMembers);
        listView.setAdapter(adapter);
    }

    public void onClickSaveTeamName(View view) {
        Log.d(TAG, "On click save team name start");
        teamNameStr = teamName.getText().toString();
        if(teamNameStr.isEmpty()){
            Toast.makeText(this, "Please enter a team name", Toast.LENGTH_LONG).show();
        }
        progressDialog = new ProgressDialog(this);
        Log.d(TAG, "progressDialog start");
        progressDialog.setTitle("We now change your team name");
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        ParseQuery<ParseObject> query =  ParseQuery.getQuery("Team");
        query.whereEqualTo("Manager",currentUser.getEmail());
        Log.d(TAG, "findInBackground start");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                Log.d(TAG, "findInBackground done");
                if (e == null) {
                    if (objects.size() != 1) {
                        Log.d(TAG, "Object size" + objects.size());
                        //todo ?
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
                                    //todo
                                    Log.d(TAG, "saveInBackground exception", e);
                                }
                            }
                        });
                    }
                } else {
                    //  objectRetrievalFailed();
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
        getMenuInflater().inflate(R.menu.edit_team_activity_nav, menu);
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


