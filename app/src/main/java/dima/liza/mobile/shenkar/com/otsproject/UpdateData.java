package dima.liza.mobile.shenkar.com.otsproject;

import android.content.Context;
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
        queryTask.whereEqualTo("taskEmployee",currentUser.getUsername());
        if(wasUpdated) {
            if (isManager) {
                Log.d(TAG,"Update task for Manager");
                queryTask.whereEqualTo("updateForManager", true);
            } else {
                Log.d(TAG, "Update task for Employee");
                queryTask.whereEqualTo("updateForEmployee", true);  //wtf??? Not work!!
                queryTask.whereEqualTo("taskEmployee",currentUser.getEmail());
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
                        Task oldTask = dataAccess.getTaskById(parseId);
                            Task newTask = new Task(taskHeader, taskDescription, employee, deadline, status, category, location, photoRequire, parseId, deadlineStr);
                            if(oldTask!=null) {
                            if (oldTask.getDeadline().compareTo(newTask.getDeadline()) != 0) {
                                NotificationControl.notificationNow("Deadline of task change", taskHeader, R.drawable.ic_menu_send, parseId.hashCode(), context);
                            }
                            if (!taskDescription.equals(oldTask.getTaskDescription())) {
                                NotificationControl.notificationNow("Task description  change", taskHeader, R.drawable.ic_menu_send, taskDescription.hashCode(), context);
                            }
                            if (!taskHeader.equals(oldTask.getTaskHeader())) {
                                NotificationControl.notificationNow("Task description  change", taskHeader, R.drawable.ic_menu_send, taskHeader.hashCode(), context);
                            }
                            if (!status.equals("cancel")) {
                                NotificationControl.notificationNow("Task cancel ", taskHeader, R.drawable.ic_menu_send, status.hashCode(), context);
                            }
                            if (!category.equals(oldTask.getCategory())) {
                                NotificationControl.notificationNow("Task category  change", taskHeader, R.drawable.ic_menu_send, category.hashCode(), context);
                            }
                            if (!location.equals(oldTask.getLocation())) {
                                NotificationControl.notificationNow("Task location  change", taskHeader, R.drawable.ic_menu_send, location.hashCode(), context);
                            }
                        }

                        dataAccess.insertTask(newTask);
                        Log.d(TAG, "Update task done. Id is a:" + parseId);
                      //  int number = (int) (Math.random()*100);

                        if (isManager) {
                            NotificationControl.notificationNow("You have update status task",taskHeader,R.drawable.ic_menu_send,2,context);
                            Log.d(TAG,"Update parse filed:updateForManager to false");
                            object.put("updateForManager", false);
                        } else {
                            NotificationControl.notificationNow("You have new task",taskHeader,R.drawable.ic_menu_send,2,context);
                            Log.d(TAG, "Update parse filed:updateForEmployee to false");
                            object.put("updateForEmployee", false);
                        }
                        object.saveInBackground();  //warning!!!
                        // }
                    }

                } else {
                    Log.d(TAG, "findInBackground exception:", e);
                }

            }
        });
        if(numberOfUpdateTask>0) {
            NotificationControl.notificationNow("Update/new task", "" + numberOfUpdateTask, R.drawable.ic_launcher, 1, context);
        }
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

        // update time of last update to current time
        /*
        Calendar calendar = Calendar.getInstance();
        SharedPreferences lastUpdateData = context.getSharedPreferences("DateDataUpdate", context.MODE_PRIVATE);
        SharedPreferences.Editor ed = lastUpdateData.edit();
        Date date =  calendar.getTime();
            ed.putString("lastEmployeeDateUpdate", date.toString());
            Log.i(TAG,"Date:"+date.toString());
            */
    }
}
