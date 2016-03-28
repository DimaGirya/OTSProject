package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.SynchronizationService;
import dima.liza.mobile.shenkar.com.otsproject.UpdateData;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;
import dima.liza.mobile.shenkar.com.otsproject.task.data.AdapterTaskForEmployee;
import dima.liza.mobile.shenkar.com.otsproject.task.data.Task;
import dima.liza.mobile.shenkar.com.otsproject.task.data.ViewRowTask;

public class TaskShowEmployeeActivity extends AppCompatActivity {
    private static final String TAG = "TaskShowEmployeeActivity";
    TextView numberOfTask;
    CheckBox checkBox;
    ListView listView;
    Boolean getPastTask;
    ParseUser currentUser;
    ListAdapter adapter;
    List<Task> listOfTask;
    DataAccess dataAccess;
    private String taskSelectedId;
    private static final int ID_REPORT_TASK = 0;
    private String taskSelectedIdParse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_show_employee);
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
        fab.hide();
        dataAccess = DataAccess.getInstatnce(this);
        checkBox = (CheckBox) findViewById(R.id.checkBoxPastTask);
        numberOfTask = (TextView) findViewById(R.id.numberOfTask);
        listView = (ListView) findViewById(R.id.listViewTask);
        currentUser = ParseUser.getCurrentUser();
        getPastTask = checkBox.isChecked();
        UpdateData updateData = UpdateData.getInstance();
        updateData.updateTaskList(this,false);
        onResume();
    }

    @Override
    protected void onResume() {
        listOfTask = dataAccess.getAllTask(checkBox.isChecked());
        numberOfTask.setText(getString(R.string.numberOfTask) + listOfTask.size());
        adapter = new AdapterTaskForEmployee(this,listOfTask);
        listView = (ListView) findViewById(R.id.listViewTask);
        listView.setAdapter(adapter);
//        listView.setOnItemLongClickListener((AdapterView.OnItemLongClickListener) adapter); //warning
        registerForContextMenu(listView);
        super.onResume();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        ListView lv = (ListView) v;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Task task = (Task) lv.getItemAtPosition(acmi.position);
        taskSelectedIdParse = task.getParseId();
        menu.add(Menu.NONE, ID_REPORT_TASK, Menu.NONE, "Report task");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ID_REPORT_TASK:{
                Intent intent = new Intent(this,ReportTaskActivity.class);
        //        intent.putExtra("editTask",true);
                intent.putExtra("taskId",taskSelectedIdParse);
                startActivity(intent);
             //   Log.d(TAG,"ID_EDIT_TASK");
             //   Log.d(TAG,taskSelectedId);
                break;
            }
        }
        return super.onContextItemSelected(item);
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

    public void checkBoxShowOnlyActualTask(View view) {
        onResume();
    }
}
