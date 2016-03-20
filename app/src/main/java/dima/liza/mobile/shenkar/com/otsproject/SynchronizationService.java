package dima.liza.mobile.shenkar.com.otsproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.activity.ShowTaskManagerActivity;
import dima.liza.mobile.shenkar.com.otsproject.activity.SignUpManagerActivity;
import dima.liza.mobile.shenkar.com.otsproject.activity.TaskShowEmployeeActivity;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;
import dima.liza.mobile.shenkar.com.otsproject.task.data.Task;

public class SynchronizationService extends Service {
    private static final String TAG = "SynchronizationService";
    private static final int NOTIFICATION_NUMBER = 768 ;
    private static final int NEW_TASK = 0;
    private static final int TASK_STATUS_UPDATE = 1;
    private static final int TASK_CANCEL = 3;
    private boolean isManager;
    SharedPreferences sharedPreferences;
    private String currentUserName;
    private  ParseUser currentUser;
    Date lastUpdate;
    Handler handler;
    private int numberOfUpdateTask;
    UpdateData updateData;
    public SynchronizationService() {

    }
    @Override
    public void onCreate() {
        lastUpdate = getLastUpdateDate();
        Log.i(TAG, "Service: onCreate");


        ParseUser currentUser = ParseUser.getCurrentUser();
        isManager = currentUser.getBoolean("isManager");
        Log.d(TAG, "isManager:"+isManager);
        currentUserName = currentUser.getUsername();
        updateData = UpdateData.getInstance();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle("Hello " + currentUserName);
        PendingIntent contentIntent;
        if(isManager) {
             contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, ShowTaskManagerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentText("You not have new task status update");
        }
        else{
             contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, TaskShowEmployeeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentText("You not have new task yet");
        }
        mBuilder.setContentIntent(contentIntent);
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        startForeground(NOTIFICATION_NUMBER,  mBuilder.build());
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case NEW_TASK:
                        Log.d(TAG,"NEW TASK");
                        break;
                    case TASK_STATUS_UPDATE:

                        break;
                    case TASK_CANCEL:

                        break;
                }
            };
        };
       // handler.sendEmptyMessage(STATUS_NONE);
        service();
    }

    private Date getLastUpdateDate() {
        sharedPreferences = getSharedPreferences("DateTimSave", MODE_PRIVATE);
        String savedDate = sharedPreferences.getString("DateTimSave","");
        Date date;
        if(savedDate.equals("")){
            GregorianCalendar gr = new GregorianCalendar();
            gr.set(2015,1,1);
            date = gr.getTime();
            Log.d(TAG,"getLastUpdateDate:NONE");
        }
        else{
            try {
                SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
                date = dateFormat.parse(savedDate);
                Log.d(TAG,"getLastUpdateDate:"+date.toString());
            } catch (Exception e) {
                Log.d(TAG,"Parse date exception.Parse string:"+savedDate,e);
                return null;
            }
        }
        return date;
    }

    private void service() {
        Thread t = new Thread(new Runnable(){
            public void run() {
                while (true) {
                    if (isManager) {
                        updateData.updateTaskList(SynchronizationService.this,isManager);
                        updateData.updateEmployeeList(SynchronizationService.this);
                    } else {
                        updateData.updateTaskList(SynchronizationService.this,isManager);
                    }
                    updateDone();
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    private void updateDone() {
        Date date = Calendar.getInstance().getTime();
        sharedPreferences = getSharedPreferences("DateTimSave", MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        String dateStr = dateFormat.format(date);
        ed.putString("date", dateStr);
        ed.commit();
        Log.d(TAG, "Update done.Update time:" + dateStr);
    }
    /*
    private void updateTaskList(final boolean isManager) {
        final DataAccess dataAccess = DataAccess.getInstatnce(this);
        currentUser = ParseUser.getCurrentUser();
        boolean wasUpdated = (dataAccess.getAllTask(true).size()>0);
        ParseQuery<ParseObject> queryTask = ParseQuery.getQuery("Task");
        queryTask.whereEqualTo("taskManager", currentUser.getEmail());
        if(wasUpdated) {
            if (isManager) {
                queryTask.whereEqualTo("updateForManager", true);
            } else {
                queryTask.whereEqualTo("updateForEmployee", true);
            }
        }
     //   Log.d(TAG, lastUpdate.toString());

        queryTask.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                numberOfUpdateTask = objects.size();
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
                    ParseObject object;
                    for (int i = 0; i < objects.size(); i++) {
                        //   if(!lastUpdate.before(objects.get(i).getUpdatedAt())){
                        object = objects.get(i);
                        taskDescription = object.getString("taskDescription");
                        taskHeader = object.getString("taskHeader");
                        employee = object.getString("taskEmployee");
                        deadline = object.getDate("taskDate");
                        deadlineStr = dateFormat.format(deadline);
                        status = object.getString("status");
                        category = object.getString("taskCategory");
                        location = object.getString("taskLocation");
                        parseId = object.getObjectId();
                        photoRequire = object.getBoolean("photoRequire");
                        //todo compare task before end after and notification change
                        dataAccess.insertTask(new Task(taskHeader, taskDescription, employee, deadline, status, category, location, photoRequire, parseId, deadlineStr));
                        Log.d(TAG, "Update task:" + parseId);
                        if (isManager) {
                            object.put("updateForManager", false);
                        } else {
                            object.put("updateForEmployee", false);
                        }
                        object.saveInBackground();
                        // }
                    }

                } else {
                    Log.d(TAG, "findInBackground exception:", e);
                }

            }
        });
        Log.d(TAG, "updateManagerTask");
        if(numberOfUpdateTask>0) {
            NotificationControl.notificationNow("Update/new task", "" + numberOfUpdateTask, R.drawable.ic_launcher, 1, this);
        }
    }
    */


    private void updateEmployeeList() {
        Log.d(TAG,"updateEmployeeList");
      NotificationControl.notificationNow("updateEmployeeList","updateEmployeeList",R.drawable.ic_launcher,2,this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

}
