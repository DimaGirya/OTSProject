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

    public static UpdateData getInstance() {
        return ourInstance;
    }

    private UpdateData() {
    }

    public void updateTaskList(final Context context,final boolean isManager) {

        final DataAccess dataAccess = DataAccess.getInstatnce(context);
        ParseUser currentUser = ParseUser.getCurrentUser();
        boolean wasUpdated = (dataAccess.getAllTask(true).size()>0);
        Log.d(TAG,"wasUpdated:"+wasUpdated);
        Log.d(TAG,"dataAccess.getAllTask(true).size():"+dataAccess.getAllTask(true).size());
        ParseQuery<ParseObject> queryTask = ParseQuery.getQuery("Task");
        queryTask.whereEqualTo("taskManager", currentUser.get("manager"));
        if(wasUpdated) {
            if (isManager) {
                Log.d(TAG,"Update task for Manager");
                queryTask.whereEqualTo("updateForManager", true);
            } else {
                Log.d(TAG, "Update task for Employee");
                queryTask.whereEqualTo("updateForEmployee", true);
                queryTask.whereEqualTo("taskEmployee",currentUser.getUsername());
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
                        photoRequire = object.getBoolean("photoRequire");
                        priority = object.getString("priority");
                        Task oldTask = dataAccess.getTaskById(parseId);
                         Task  newTask = new Task (taskHeader,taskDescription,employee,deadline,priority,status,category,location,photoRequire,parseId,deadlineStr);
                        String taskSelectedIdParse = newTask.getParseId();
                        PendingIntent pendingIntent;
                        Intent intent = new Intent(context,ReportTaskActivity.class);
                        intent.putExtra("taskId",taskSelectedIdParse);
                        pendingIntent =  PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        if(!isManager){

                            if(oldTask!=null) {
                                if (oldTask.getDeadline().compareTo(newTask.getDeadline()) != 0) {
                                    NotificationControl.notificationNow("Deadline of task change", taskHeader, R.drawable.ic_menu_send, parseId.hashCode(), context,pendingIntent);
                                }
                                if (!taskDescription.equals(oldTask.getTaskDescription())) {
                                    NotificationControl.notificationNow("Task description  change", taskHeader, R.drawable.ic_menu_send, taskDescription.hashCode(), context,pendingIntent);
                                }
                                if (!taskHeader.equals(oldTask.getTaskHeader())) {
                                    NotificationControl.notificationNow("Task description  change", taskHeader, R.drawable.ic_menu_send, taskHeader.hashCode(), context,pendingIntent);
                                }
                                if (!category.equals(oldTask.getCategory())) {
                                    NotificationControl.notificationNow("Task category  change", taskHeader, R.drawable.ic_menu_send, category.hashCode(), context,pendingIntent);
                                }
                                if (!location.equals(oldTask.getLocation())) {
                                    NotificationControl.notificationNow("Task location  change", taskHeader, R.drawable.ic_menu_send, location.hashCode(), context,pendingIntent);
                                }
                                if (!status.equals("cancel")) {
                                    NotificationControl.notificationNow("Task cancel ", taskHeader, R.drawable.ic_menu_send, status.hashCode(), context,pendingIntent);
                                }
                            }
                               else{
                                NotificationControl.notificationNow("You have new task todo.Enjoy! ", taskHeader, R.drawable.ic_menu_send, taskHeader.hashCode(), context,pendingIntent);
                            }
                        }
                        else{
                               if(oldTask!=null) {
                                   if (!status.equals(oldTask.getStatus())) {
                                       NotificationControl.notificationNow("Employee change status of task ", "Task:"+taskHeader +" .New status:"+ status, R.drawable.ic_menu_send, status.hashCode(), context,pendingIntent);
                                   }
                               }
                           }

                        dataAccess.insertTask(newTask);
                        Log.d(TAG, "Update task done. Id is a:" + parseId);
                        if (isManager) {
                            NotificationControl.notificationNow("You have update status task",taskHeader,R.drawable.ic_menu_send,2,context,null);
                            Log.d(TAG,"Update parse filed:updateForManager to false");
                            object.put("updateForManager", false);
                        } else {
                            NotificationControl.notificationNow("You have new task",taskHeader,R.drawable.ic_menu_send,2,context,null);
                            Log.d(TAG, "Update parse filed:updateForEmployee to false");
                            object.put("updateForEmployee", false);
                        }
                        object.saveInBackground();
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
       // need to get all employees

            ParseQuery<ParseUser> queryEmployee = ParseUser.getQuery();
            queryEmployee.whereEqualTo("manager", currentUser.getEmail());
            queryEmployee.whereNotEqualTo("email", currentUser.getEmail());
            if(numberOfEmployee != 0) {
                queryEmployee.whereEqualTo("statusEmployeeChange",true);
                Log.d(TAG, "Need to update  employees list");
            }
            else{
                Log.d(TAG, "Need to download all employees");
            }
            queryEmployee.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        Log.e(TAG, "Active employee download:" + objects.size());
                        Employee employee;
                        for (int i = 0; i < objects.size(); i++) {
                            dataAccess.deleteEmployee(objects.get(i).getEmail());   //warning
                            employee = new Employee(objects.get(i).getUsername(), objects.get(i).getEmail(), objects.get(i).getString("phoneNumber"), "registered", objects.get(i).getInt("taskCounter"));
                            dataAccess.insertEmployee(employee);
                            if(numberOfEmployee != 0) {
                                objects.get(i).put("statusEmployeeChange",false);
                                objects.get(i).saveInBackground();

                            }
                        }
                    } else {
                        Toast.makeText(context, "Connection problem.Try again later", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "FindCallback exception", e);
                    }
                }
            });


        ParseQuery<ParseObject> queryNewEmployee = ParseQuery.getQuery("NewEmployee");  // get all employee from class NewEmployee
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
                        Toast.makeText(context, "Connection problem.Try again later", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "findInBackground exception:", e);
                    }
                }
            });
    }
}
