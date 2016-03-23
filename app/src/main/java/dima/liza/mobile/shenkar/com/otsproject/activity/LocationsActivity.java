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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import dima.liza.mobile.shenkar.com.otsproject.NotificationControl;
import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.Employee;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;

public class LocationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static String TAG  = "LocationsActivity";
    private ProgressDialog progressDialog;
    ParseUser user = ParseUser.getCurrentUser();
    String[] allLocations;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
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
        makeList();

//        ParseUser user = ParseUser.getCurrentUser();
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("location");
//        query.whereEqualTo("manager", user.getEmail());
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> locations, ParseException e) {
//                if (e == null) {
//                    Log.d(TAG,"locations size:"+locations.size());
//                    if(locations.isEmpty()){
//                        Toast.makeText(LocationsActivity.this, "Locations not found.", Toast.LENGTH_LONG).show();
//                    }
//                    else {
//                        allLocations = new String[locations.size()];
//                        for(int i=0;i<locations.size();i++){
//                            //allLocations.add(locations.get(i).getString("location"));
//                            allLocations[i] = locations.get(i).getString("location");
//                        }
//                        populate();
//
//                    }
//                } else {
//                    Toast.makeText(LocationsActivity.this, "Something went wrong, try again later.", Toast.LENGTH_LONG).show();
//                    Log.d(TAG, "exception:", e);
//                }
//            }
//        });
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
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

    public void makeList(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("location");
        query.whereEqualTo("manager", user.getEmail());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> locations, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "locations size:" + locations.size());
                    if (locations.isEmpty()) {
                        Toast.makeText(LocationsActivity.this, "Locations not found.", Toast.LENGTH_LONG).show();
                    } else {
                        allLocations = new String[locations.size()];
                        for (int i = 0; i < locations.size(); i++) {
                            //allLocations.add(locations.get(i).getString("location"));
                            allLocations[i] = locations.get(i).getString("location");
                        }
                        populate();
                    }
                } else {
                    Toast.makeText(LocationsActivity.this, "Something went wrong, try again later.", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "exception:", e);
                }
            }
        });
    }

    public void populate(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allLocations);
        ListView list = (ListView) findViewById(R.id.LocationsListView);
        list.setAdapter(adapter);
    }

    public void addLocationClicked(View view){
        EditText newLocationInput = (EditText) findViewById(R.id.editTextNewLocationName);
        String locationStr = newLocationInput.getText().toString();
        ParseUser user = ParseUser.getCurrentUser();
        ParseObject newLocation = new ParseObject("location");
        newLocation.put("location", locationStr);
        newLocation.put("manager", user.getEmail());
        newLocation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    onResume();
                } else {
                    Toast.makeText(LocationsActivity.this, "Something went wrong, try again later.", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "exception:", e);
                }
            }
        });
    }

    @Override
    public void onResume(){
        makeList();
        super.onResume();
    }

}
