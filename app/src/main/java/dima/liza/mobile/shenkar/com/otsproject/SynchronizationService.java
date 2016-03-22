package dima.liza.mobile.shenkar.com.otsproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import dima.liza.mobile.shenkar.com.otsproject.activity.ReportTaskActivity;
import dima.liza.mobile.shenkar.com.otsproject.activity.ShowTaskManagerActivity;
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
        Log.i(TAG, "SynchronizationService: onCreate");


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
     /*
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
        */
        service();
    }

    private Date getLastUpdateDate() {
        sharedPreferences = getSharedPreferences("DateTimSave", MODE_PRIVATE);
        String savedDate = sharedPreferences.getString("DateTimSave", "");
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
        final Context context = SynchronizationService.this;
        final DataAccess dataAccess = DataAccess.getInstatnce(context);
        Thread t = new Thread(new Runnable(){
            public void run() {
                while (true) {
                    try {
                        Parse.initialize(SynchronizationService.this);
                    }
                    catch (Exception e){

                    }

                    if (isManager) {
                        updateData.updateTaskList(SynchronizationService.this,isManager);
                        updateData.updateEmployeeList(SynchronizationService.this);

                    } else {
                        updateData.updateTaskList(SynchronizationService.this,isManager);
                    }
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                    mBuilder.setSmallIcon(R.drawable.ic_launcher);
                    mBuilder.setContentTitle("Hello " + currentUserName);
                    PendingIntent contentIntent;
                    if(isManager) {
                        contentIntent = PendingIntent.getActivity(context, 0,
                                new Intent(SynchronizationService.this, ShowTaskManagerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentText("Your team have "+dataAccess.getNumberOfTask(false)+ " task now");
                    }
                    else{
                        contentIntent = PendingIntent.getActivity(context, 0,
                                new Intent(context, TaskShowEmployeeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentText("Your  have a "+dataAccess.getNumberOfTask(false)+ " task now");
                    }
                    mBuilder.setContentIntent(contentIntent);
                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                    startForeground(NOTIFICATION_NUMBER,  mBuilder.build());
                    checkDeadline();
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

    private void checkDeadline() {
        DataAccess dataAccess = DataAccess.getInstatnce(this);
        List<Task> taskList = dataAccess.getAllTask(false);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date now = cal.getTime();
        Log.d(TAG,"Date time now:"+now.toString());
        for(int i = 0;i < taskList.size();i++){
            Task task = taskList.get(i);
            if(now.after(task.getDeadline())){
                ParseObject taskObject = new ParseObject("Task");
                taskObject.setObjectId(taskList.get(i).getParseId());
                taskObject.put("status", "late");
                task.setStatus("late");
                dataAccess.updateTask(task);

                taskObject.saveInBackground();
                String taskSelectedIdParse = task.getParseId();
                PendingIntent pendingIntent;
                Intent intent = new Intent(this,ReportTaskActivity.class);
                intent.putExtra("taskId", taskSelectedIdParse);
                pendingIntent =  PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if(isManager){
                    NotificationControl.notificationNow("Your employee late in task","Employee:"+task.getEmployee()+" Task:"+task.getTaskHeader(),
                            R.drawable.ic_menu_send,task.getParseId().hashCode(),this,pendingIntent);
                }
                else{
                    NotificationControl.notificationNow("You late in task","Employee:"+task.getEmployee()+" Task:"+task.getTaskHeader(),
                            R.drawable.ic_menu_send,task.getParseId().hashCode(),this,pendingIntent);
                }
            }
        }
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


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return SynchronizationService.START_STICKY;
    }

}
