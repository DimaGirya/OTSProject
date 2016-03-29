package dima.liza.mobile.shenkar.com.otsproject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.activity.ReportTaskActivity;
import dima.liza.mobile.shenkar.com.otsproject.activity.ShowTaskManagerActivity;
import dima.liza.mobile.shenkar.com.otsproject.activity.TaskShowEmployeeActivity;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.Employee;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.EmployeeToAdd;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;
import dima.liza.mobile.shenkar.com.otsproject.task.data.Task;

/**
 * Created by Girya on 18/03/2016.
 */
public class UpdateData {
    int numberOfUpdateTask;
    private static final String TAG = "UpdateData";
    private static UpdateData ourInstance = new UpdateData();
    private int numberOfEmployee;
    int drawable = R.drawable.ic_launcher;
    public static UpdateData getInstance() {
        return ourInstance;
    }

    private UpdateData() {
    }

    public void updateTaskList(final Context context,final boolean isManager) {

        final DataAccess dataAccess = DataAccess.getInstatnce(context);
        ParseUser currentUser = ParseUser.getCurrentUser();
        boolean wasUpdated = (dataAccess.getAllTask(true).size()>0);    //replace to shared preference
        Log.d(TAG,"wasUpdated:"+wasUpdated);
        Log.d(TAG,"dataAccess.getAllTask(true).size():"+dataAccess.getAllTask(true).size());
        ParseQuery<ParseObject> queryTask = ParseQuery.getQuery("Task");
        queryTask.whereEqualTo("taskManager", currentUser.get("manager"));
        if(!isManager){
            queryTask.whereEqualTo("taskEmployee",currentUser.getUsername());
        }
        if(wasUpdated) {
            if (isManager) {
                Log.d(TAG,"Update task for Manager");
                queryTask.whereEqualTo("updateForManager", true);
            } else {
                Log.d(TAG, "Update task for Employee");
                queryTask.whereEqualTo("updateForEmployee", true);

            }
        }
        queryTask.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                numberOfUpdateTask = objects.size();
                Log.e(TAG, "Number  of task download from parse:" + numberOfUpdateTask);
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
                    String priority;
                    boolean photoRequire;
                    SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
                    ParseObject object;
                    int countOfNewTask = 0;
                    Task notificationTask = null;

                    for (int i = 0; i < objects.size(); i++) {
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
                        photoRequire = object.getBoolean("requirePhoto");
                        priority = object.getString("priority");
                        Task oldTask = dataAccess.getTaskById(parseId);
                         Task  newTask = new Task (taskHeader,taskDescription,employee,deadline,priority,status,category,location,photoRequire,parseId,deadlineStr);
                        String taskSelectedIdParse = newTask.getParseId();
                        PendingIntent pendingIntent;
                            Intent intent = new Intent(context, ReportTaskActivity.class);
                            intent.putExtra("taskId", taskSelectedIdParse);
                            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        if(!isManager){
                            if(oldTask!=null) {
                                if (oldTask.getDeadline().compareTo(newTask.getDeadline()) != 0) {
                                    NotificationControl.notificationNow(context.getString(R.string.deadlineChange), taskHeader, drawable, parseId.hashCode(), context,pendingIntent);
                                }
                                if (!taskDescription.equals(oldTask.getTaskDescription())) {
                                    NotificationControl.notificationNow(context.getString(R.string.descriptionChange), taskHeader, drawable, taskDescription.hashCode(), context,pendingIntent);
                                }
                                if (!taskHeader.equals(oldTask.getTaskHeader())) {
                                    NotificationControl.notificationNow(context.getString(R.string.headerChange), taskHeader, drawable, taskHeader.hashCode(), context,pendingIntent);
                                }
                                 if (!category.equals(oldTask.getCategory())) {
                                     NotificationControl.notificationNow(context.getString(R.string.categoryChange), taskHeader, drawable, category.hashCode(), context,pendingIntent);
                                 }
                                 if (!location.equals(oldTask.getLocation())) {
                                     NotificationControl.notificationNow(context.getString(R.string.locationChange), taskHeader,drawable, location.hashCode(), context,pendingIntent);
                                 }
                                if(!oldTask.getStatus().equals(newTask.getStatus())){
                                      if (newTask.getStatus().equals("cancel")) {
                                         NotificationControl.notificationNow(context.getString(R.string.taskCancelNotification), taskHeader, drawable, status.hashCode(), context,pendingIntent);
                                      }
                                }

                                }
                               else{
                                String newStatus  = newTask.getStatus();
                                if(newStatus.compareTo("cancel") == 0  || newStatus.compareTo("late") == 0 || newStatus.compareTo("done")  == 0 || newStatus.compareTo("reject") == 0 )
                                {
                                    Log.d(TAG,"Status cancel,late or done");
                                }
                                else {
                                    Log.d(TAG,"countOfNewTask++");
                                    countOfNewTask++;
                                    notificationTask = newTask;
                                }
                            }
                        }
                        else{
                               if(oldTask!=null) {
                                   if (!status.equals(oldTask.getStatus())) {
                                       NotificationControl.notificationNow(context.getString(R.string.employeeChangeStatus), context.getString(R.string.ofTask)+taskHeader +context.getString(R.string.newStatusIs)+ status, drawable, status.hashCode(), context,pendingIntent);
                                   }
                               }
                           }
                        Log.d(TAG,"IsPhoto:"+newTask.isPhotoRequire());
                        dataAccess.insertTask(newTask);
                        Log.d(TAG, "Update task done. Id is a:" + parseId);
                        object.saveInBackground();
                    }
                    if(!isManager) {
                        if (countOfNewTask == 1) {
                            PendingIntent pendingIntent;
                            Intent intent = new Intent(context, ReportTaskActivity.class);
                            intent.putExtra("taskId", notificationTask.getParseId());

                            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                            NotificationControl.notificationNow(context.getString(R.string.newTaskTodo), notificationTask.getTaskHeader(), drawable, notificationTask.getTaskHeader().hashCode(), context, pendingIntent);
                        } else if (countOfNewTask != 0) {
                            PendingIntent pendingIntent;
                            Intent intent = new Intent(context, TaskShowEmployeeActivity.class);
                            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                            NotificationControl.notificationNow(context.getString(R.string.youHaveNewTasks) + countOfNewTask + context.getString(R.string.taskTodo), "", drawable, 1, context, pendingIntent);
                        }
                    }

                } else {
                    Log.d(TAG, "findInBackground exception:", e);
                }

            }
        });
    }

    public void updateEmployeeList(final Context context) {
        final DataAccess dataAccess = DataAccess.getInstatnce(context);
        ParseUser currentUser = ParseUser.getCurrentUser();
         numberOfEmployee = dataAccess.getAllRegisteredEmployeesName().length-1;
        Log.d(TAG,"numberOfEmployee:"+numberOfEmployee);
            ParseQuery<ParseUser> queryEmployee = ParseUser.getQuery();
            queryEmployee.whereEqualTo("manager", currentUser.getEmail());
            queryEmployee.whereNotEqualTo("email", currentUser.getEmail());
            queryEmployee.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        Log.e(TAG, "Active employee download:" + objects.size());
                        Employee employee;
                        for (int i = 0; i < objects.size(); i++) {
                            dataAccess.deleteEmployee(objects.get(i).getEmail());
                            employee = new Employee(objects.get(i).getUsername(), objects.get(i).getEmail(), objects.get(i).getString("phoneNumber"), "registered", objects.get(i).getInt("taskCounter"));
                            dataAccess.insertEmployee(employee);
                            if (numberOfEmployee != 0) {
                                objects.get(i).put("statusEmployeeChange", false);
                                objects.get(i).saveInBackground();

                            }
                        }
                    } else {
                        Toast.makeText(context,R.string.conectionProblem, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "FindCallback exception", e);
                    }
                }
            });


        ParseQuery<ParseObject> queryNewEmployee = ParseQuery.getQuery("NewEmployee");
        queryNewEmployee.whereEqualTo("Manager", currentUser.getEmail());
            if(numberOfEmployee == 0){
                queryNewEmployee.whereEqualTo("statusEmployeeChange",true);
            }
            queryNewEmployee.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    Log.e(TAG, "New Employee download:" + objects.size());
                    if (e == null) {
                        List<EmployeeToAdd> list = new ArrayList();
                        for (int i = 0; i < objects.size(); i++) {
                            list.add(new EmployeeToAdd(objects.get(i).getString("email"), objects.get(i).getString("numberPhone")));
                        }
                        for (int i = 0; i < objects.size(); i++) {
                            dataAccess.insertEmployee(new Employee(list.get(i)));
                            objects.get(i).put("statusEmployeeChange", false);
                            objects.get(i).saveInBackground();
                        }
                    } else {
                        Toast.makeText(context, R.string.conectionProblem, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "findInBackground exception:", e);
                    }
                }
            });
    }


    public void getLocationFromParse(final Context context){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("location");
        ParseUser user = ParseUser.getCurrentUser();
        final String[][] allLocations = new String[1][1];
        query.whereEqualTo("manager", user.getEmail());
        final DataAccess dataAccess = DataAccess.getInstatnce(context);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> locations, ParseException e) {
                if (e == null) {
                    if (locations.isEmpty()) {
                        Toast.makeText(context, R.string.locationNotFount, Toast.LENGTH_LONG).show();
                    } else {
                        allLocations[0] = new String[locations.size()];
                        for (int i = 0; i < locations.size(); i++) {
                            allLocations[0][i] = locations.get(i).getString("location");
                        }
                        dataAccess.insertLocations(allLocations[0]);
                    }
                } else {
                    Toast.makeText(context, R.string.somethingWentWrong, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "exception:", e);
                }
            }
        });
    }
}
