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
    private boolean isManager;
    SharedPreferences sharedPreferences;
    private String currentUserName;
    Date lastUpdate;
    UpdateData updateData;
    public SynchronizationService() {

    }
    @Override
    public void onCreate() {
        try {
            lastUpdate = getLastUpdateDate();
            Log.i(TAG, "SynchronizationService: onCreate");
            ParseUser currentUser = ParseUser.getCurrentUser();
            isManager = currentUser.getBoolean("isManager");
            Log.d(TAG, "isManager:" + isManager);
            currentUserName = currentUser.getUsername();
            updateData = UpdateData.getInstance();
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.ic_launcher);
            mBuilder.setContentTitle("Hello " + currentUserName);
            PendingIntent contentIntent;
            if (isManager) {
                contentIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, ShowTaskManagerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentText(getString(R.string.noStatus));
            } else {
                contentIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, TaskShowEmployeeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentText(getString(R.string.noTaskYet));
            }
            mBuilder.setContentIntent(contentIntent);
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
            startForeground(NOTIFICATION_NUMBER, mBuilder.build());
            service();
        }
        catch (Exception e){
            Log.d(TAG,"Exception in  service:",e);
            stopSelf();
        }
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
                    try{
                        loopService(context,dataAccess);
                    }
                    catch (Exception e){
                      Log.d(TAG,"Service is shutdown",e);
                    }
            }
        });
        t.start();
    }


    private void loopService(Context context,DataAccess dataAccess){
        while (true) {
            try {
                Parse.initialize(SynchronizationService.this);
            }
            catch (Exception e){
                Log.d(TAG,"Exception Parse.initialize",e);
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
                mBuilder.setContentText(getString(R.string.yourTeamHave)+dataAccess.getNumberOfTask(false)+ getString(R.string.taskNow));
            }
            else{
                contentIntent = PendingIntent.getActivity(context, 0,
                        new Intent(context, TaskShowEmployeeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentText(getString(R.string.youHave)+dataAccess.getNumberOfTask(false)+ getString(R.string.taskNow));
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
                sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
                int minute = sharedPreferences.getInt("UpdateTime",5);
                Log.d(TAG,"Service thread sleep to "+minute+" minutes");
                Thread.sleep(1000 * 60 * minute);
            } catch (InterruptedException e) {
               Log.d(TAG,"InterruptedException",e);
            }
        }
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
                            R.drawable.ic_launcher,task.getParseId().hashCode(),this,pendingIntent);
                }
                else{
                    NotificationControl.notificationNow("You late in task","Employee:"+task.getEmployee()+" Task:"+task.getTaskHeader(),
                            R.drawable.ic_launcher,task.getParseId().hashCode(),this,pendingIntent);
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
