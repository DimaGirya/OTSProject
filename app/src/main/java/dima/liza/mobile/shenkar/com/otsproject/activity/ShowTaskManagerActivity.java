package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
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
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.AboutActivity;
import dima.liza.mobile.shenkar.com.otsproject.ManagerValidation;
import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.SynchronizationService;
import dima.liza.mobile.shenkar.com.otsproject.UpdateData;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;
import dima.liza.mobile.shenkar.com.otsproject.task.data.AdapterTaskForManager;
import dima.liza.mobile.shenkar.com.otsproject.task.data.Task;

public class ShowTaskManagerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "ShowTaskManagerActivity" ;
    private static final int ID_EDIT_TASK = 0 ;
    private static final int ID_CANCEL_TASK = 1 ;
    private static final int ID_VIEW_TASK = 2;
    private TextView numberOfTask;
    private CheckBox checkBox;
    private ListView listView;
    private  Boolean isManager;
    private Boolean getPastTask;
    private ParseUser currentUser;
    private ListAdapter adapter;
    private List<Task> listOfTask;
    private DataAccess dataAccess;
    private String taskSelectedId;
    private Task task;
    private TextView userName,userEmail;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerValidation.checkRegisteredEmployee(ShowTaskManagerActivity.this, dataAccess);
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        dataAccess = DataAccess.getInstatnce(this);
        checkBox = (CheckBox) findViewById(R.id.checkBoxPastTask);
        numberOfTask = (TextView) findViewById(R.id.numberOfTask);
        listView = (ListView) findViewById(R.id.listViewTask);
        listView.setAdapter(adapter);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final DataAccess dataAccess = DataAccess.getInstatnce(this);
        currentUser = ParseUser.getCurrentUser();
        getPastTask = checkBox.isChecked();
        isManager = currentUser.getBoolean("isManager");
        UpdateData updateData = UpdateData.getInstance();
        updateData.updateTaskList(this,isManager);
        onResume();
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

            if(ParseUser.getCurrentUser()==null){
                finish();
            }
        listOfTask = dataAccess.getAllTask(checkBox.isChecked());
        if(listOfTask.size()==0){
            String noTask [] = new String[1];
            noTask[0] = "No current task";
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,noTask);
            listView = (ListView) findViewById(R.id.listViewTask);
            listView.setAdapter(adapter);
            unregisterForContextMenu(listView);
        }
        else {
            numberOfTask.setText(getString(R.string.numberOfTask) + listOfTask.size());
            adapter = new AdapterTaskForManager(this, listOfTask);
            listView = (ListView) findViewById(R.id.listViewTask);
            listView.setAdapter(adapter);
            registerForContextMenu(listView);
        }
            super.onResume();
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        ListView lv = (ListView) v;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
         task = (Task) lv.getItemAtPosition(acmi.position);
        taskSelectedId = task.getParseId();

        String status = task.getStatus();
        if(status.compareTo("cancel") == 0  || status.compareTo("late") == 0 || status.compareTo("done")  == 0 || status.compareTo("reject") == 0  ){
            menu.add(Menu.NONE,ID_VIEW_TASK,Menu.NONE, R.string.viewTask);
        }
        else {
            menu.add(Menu.NONE,ID_VIEW_TASK,Menu.NONE, R.string.viewTask);
            menu.add(Menu.NONE, ID_EDIT_TASK, Menu.NONE, R.string.editTask);
            menu.add(Menu.NONE, ID_CANCEL_TASK, Menu.NONE, R.string.cancelTask);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case ID_VIEW_TASK:{
                Intent intent = new Intent(this,ReportTaskActivity.class);
              //  intent.putExtra("editTask",true);
                intent.putExtra("taskId",taskSelectedId);
                startActivity(intent);
                Log.d(TAG,"ID_EDIT_TASK");
                Log.d(TAG,taskSelectedId);
                break;
            }
            case ID_EDIT_TASK:{
                Intent intent = new Intent(this,AddTaskActivity.class);
                intent.putExtra("editTask",true);
                intent.putExtra("taskId",taskSelectedId);
                startActivity(intent);
                Log.d(TAG,"ID_EDIT_TASK");
                Log.d(TAG,taskSelectedId);
                break;
            }
            case ID_CANCEL_TASK:{
                Log.d(TAG,"ID_CANCEL_TASK");
                Log.d(TAG,taskSelectedId);
                ParseObject parseTask = ParseObject.createWithoutData("Task", taskSelectedId);
                parseTask.put("status","cancel");
                parseTask.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Toast.makeText(ShowTaskManagerActivity.this, R.string.taskCancelDone, Toast.LENGTH_LONG).show();
                            Task taskCancel = dataAccess.getTaskById(taskSelectedId);
                            taskCancel.setStatus("cancel");
                            dataAccess.updateTask(taskCancel);
                            onResume();
                        }
                        else{
                            Toast.makeText(ShowTaskManagerActivity.this, R.string.taskNotCancel,Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;

            }
        }
        return super.onContextItemSelected(item);
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

    public void checkBoxShowOnlyActualTask(View view) {
        onResume();
    }

    @Override
    public void onRefresh() {
        Toast.makeText(this,"Refresh",Toast.LENGTH_LONG).show();
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                UpdateData.getInstance().updateTaskList(ShowTaskManagerActivity.this,true);
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(ShowTaskManagerActivity.this, R.string.refreshFinish,Toast.LENGTH_LONG).show();
                onResume();
            }
        });
    }
}
