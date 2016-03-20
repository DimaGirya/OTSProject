package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import dima.liza.mobile.shenkar.com.otsproject.ManagerValidation;
import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.SynchronizationService;
import dima.liza.mobile.shenkar.com.otsproject.UpdateData;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;
import dima.liza.mobile.shenkar.com.otsproject.task.data.AdapterTaskForManager;
import dima.liza.mobile.shenkar.com.otsproject.task.data.Task;

public class ShowTaskManagerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "ShowTaskManagerActivity" ;
    private static final int ID_EDIT_TASK = 0 ;
    private static final int ID_CANCEL_TASK = 1 ;
    TextView numberOfTask;
    CheckBox checkBox;
    ListView listView;
    Boolean isManager;
    Boolean getPastTask;
    ParseUser currentUser;
    ListAdapter adapter;
    List<Task> listOfTask;
    DataAccess dataAccess;
    private String taskSelectedId;

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
        /*
        ParseQuery<ParseObject> queryTask = ParseQuery.getQuery("Task");
        queryTask.whereEqualTo("taskManager", currentUser.getEmail());
        queryTask.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e(TAG, "Task objects size" + objects.size());
                if (e == null) {
                    List<Task> list = new ArrayList();
                    String taskDescription;
                    String employee;
                    Date deadline;
                    String deadlineStr;
                    String status;
                    String category;
                    String location;
                    String parseId;
                    String taskHeader;
                    boolean photoRequire;
                    SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
                    for (int i = 0; i < objects.size(); i++) {
                        taskDescription = objects.get(i).getString("taskDescription");
                        taskHeader =  objects.get(i).getString("taskHeader");
                        employee = objects.get(i).getString("taskEmployee");
                        deadline = objects.get(i).getDate("taskDate");
                        deadlineStr = dateFormat.format(deadline);
                        status = objects.get(i).getString("status");
                        category = objects.get(i).getString("taskCategory");
                        location = objects.get(i).getString("taskLocation");
                        parseId = objects.get(i).getObjectId();
                        photoRequire = objects.get(i).getBoolean("photoRequire");
                        dataAccess.insertTask(new Task(taskHeader,taskDescription, employee, deadline, status, category, location, photoRequire, parseId, deadlineStr));
                    }
                    onResume();
                } else {
                    Toast.makeText(ShowTaskManagerActivity.this, "Connection problem.Try again later", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "findInBackground exception:", e);
                }
            }
        });
        */
    }
    @Override
    protected void onResume() {
        listOfTask = dataAccess.getAllTask(checkBox.isChecked());
        numberOfTask.setText(getString(R.string.numberOfTask)+listOfTask.size());
        adapter = new AdapterTaskForManager(this,listOfTask);
        listView = (ListView) findViewById(R.id.listViewTask);
        listView.setAdapter(adapter);
//        listView.setOnItemLongClickListener((AdapterView.OnItemLongClickListener) adapter); //warning
        registerForContextMenu(listView);
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
        View view = (View) v.getParent();
        TextView temp = (TextView)v.findViewById(R.id.taskId);
        taskSelectedId = temp.getText().toString();
        Log.d(TAG, "taskSelectedId:" + taskSelectedId);

            menu.add(Menu.NONE,ID_EDIT_TASK,Menu.NONE,"Edit task");
            menu.add(Menu.NONE,ID_CANCEL_TASK,Menu.NONE,"Cancel task");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case ID_EDIT_TASK:{
                Intent intent = new Intent(this,AddTaskActivity.class);
                intent.putExtra("editTask",true);
                intent.putExtra("taskId",taskSelectedId);
                startActivity(intent);
                Log.d(TAG,"ID_EDIT_TASK");
                Log.d(TAG,taskSelectedId);
                break;
            }
            case ID_CANCEL_TASK:{   // bag. Canceled not the task wrong
                Log.d(TAG,"ID_CANCEL_TASK");
                Log.d(TAG,taskSelectedId);
                //todo ask confirmation
                ParseObject parseTask = ParseObject.createWithoutData("Task", taskSelectedId);
                parseTask.put("status","cancel");
                parseTask.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Toast.makeText(ShowTaskManagerActivity.this,"Task cancel",Toast.LENGTH_LONG).show();
                            ShowTaskManagerActivity.this.onResume();
                        }
                        else{
                            Toast.makeText(ShowTaskManagerActivity.this,"Task not cancel",Toast.LENGTH_LONG).show();
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
            return true;
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


    public void checkBoxShowOnlyActualTask(View view) {
        onResume();
    }
}
