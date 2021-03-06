package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import dima.liza.mobile.shenkar.com.otsproject.AboutActivity;
import dima.liza.mobile.shenkar.com.otsproject.ManagerValidation;
import dima.liza.mobile.shenkar.com.otsproject.SynchronizationService;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.R;

public class LocationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static String TAG  = "LocationsActivity";
    private ProgressDialog progressDialog;
    private String[] allLocations;
    private DataAccess dataAccess;
    private ParseUser currentUser;
    private TextView userName,userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dataAccess = DataAccess.getInstatnce(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerValidation.checkRegisteredEmployee(LocationsActivity.this, dataAccess);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        ParseUser currentUser = ParseUser.getCurrentUser();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //getLocationFromParse();
        onResume();
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
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        }
        if(id == R.id.action_about){
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        currentUser = ParseUser.getCurrentUser();
        userName = (TextView) findViewById(R.id.userNameNav);
        userEmail = (TextView) findViewById(R.id.userEmailNav);
        userName.setText(currentUser.getUsername());
        userEmail.setText(currentUser.getEmail());
        return super.onPreparePanel(featureId, view, menu);
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

    public void populate(){
        allLocations = dataAccess.getLocations();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allLocations);
        ListView list = (ListView) findViewById(R.id.LocationsListView);
        list.setAdapter(adapter);
    }

    public void addLocationClicked(View view){
        EditText newLocationInput = (EditText) findViewById(R.id.editTextNewLocationName);
        final String locationStr = newLocationInput.getText().toString();
        ParseUser user = ParseUser.getCurrentUser();
        ParseObject newLocation = new ParseObject("location");
        newLocation.put("location", locationStr);
        newLocation.put("manager", user.getEmail());
        if(!dataAccess.insertLocation(locationStr)){
            Toast.makeText(this, R.string.locationInDateBase,Toast.LENGTH_LONG).show();
            return;
        }
        newLocation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    onResume();
                } else {
                    Toast.makeText(LocationsActivity.this, R.string.somethingWentWrong, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "exception:", e);
                }
            }
        });
    }

    @Override
    public void onResume(){
        if(ParseUser.getCurrentUser()==null){
            finish();
        }
        populate();
        super.onResume();
    }
}
