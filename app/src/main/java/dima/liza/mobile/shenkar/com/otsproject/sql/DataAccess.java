package dima.liza.mobile.shenkar.com.otsproject.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.employee.data.Employee;
import dima.liza.mobile.shenkar.com.otsproject.task.data.Task;

/**
 * Created by Girya on 01/03/2016.
 */

    public class DataAccess implements iDataAccess {

        private static final String TAG = "SQL_DATA_ACCESS";
        SQLiteDatabase database;
        private static DataAccess instance;
        private Context context;
        private DBHelper dbHelper;


        private DataAccess(Context context) {	//private constructor(singleton)
            try {
                this.context = context;
                dbHelper = new DBHelper(this.context);
            }
            catch (Exception e){
                Log.d(TAG, "Exception:", e);
            }
        }


        public static DataAccess getInstatnce(Context context) {
            if (instance == null)
                instance = new DataAccess(context);
            return instance;
        }


        @Override
        public boolean insertEmployee(Employee employee) {
            ContentValues content = new ContentValues();
            content.put(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL, employee.getEmail());
            content.put(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_NAME, employee.getName());
            content.put(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_PHONE_NUMBER, employee.getPhoneNumber());
            content.put(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_STATUS, employee.getStatus());
            content.put(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_TASK_COUNT, employee.getTaskCount());
            try {
                database = dbHelper.getReadableDatabase();
                if (database.insert(DbContract.EmployeeEntry.TABLE_NAME, null, content) == -1) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception:", e);
                return false;
            } finally {
                if (database != null) {
                    database.close();

                }
            }
        }
        @Override
        public boolean updateEmployeeStatus(Employee employee, String status) {
            ContentValues values = new ContentValues();
            String whereClause = DbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL + " = ? ";
            String whereArgs[] = new String[1];
            whereArgs[0] = employee.getEmail();
            values.put(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_STATUS,status);
            database = dbHelper.getReadableDatabase();
            try {
                if(database.update(DbContract.EmployeeEntry.TABLE_NAME, values, whereClause, whereArgs)!=1){
                    return false;
                }
                return true;
            }
            catch(Exception e){
                Log.d(TAG, "Exception:", e);
                return false;
            }
        }

        @Override
        public boolean updateEmployeeTaskCounter(Employee employee, int counter) {
            ContentValues values = new ContentValues();
            String whereClause = DbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL + " = ? ";
            String whereArgs[] = new String[1];
            whereArgs[0] = employee.getEmail();
            values.put(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_TASK_COUNT, counter);
            database = dbHelper.getReadableDatabase();
            try {
                if(database.update(DbContract.EmployeeEntry.TABLE_NAME, values, whereClause, whereArgs)!=1){
                    return false;
                }
                return true;
            }
            catch(Exception e){
                Log.d(TAG, "Exception:", e);
                return false;
            }
        }

        @Override
        public boolean deleteEmployee(Employee employee) {
            database = dbHelper.getReadableDatabase();
            String whereClause = DbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL + " = ? ";
            String whereArgs[] = new String[1];
            whereArgs[0] = employee.getEmail();
            try {

                if(database.delete(DbContract.EmployeeEntry.TABLE_NAME, whereClause, whereArgs)!=1){
                    return false;
                }
                return true;
            }
            catch(Exception e){
                Log.d(TAG, "Exception:", e);
                return false;
            }
        }

        @Override
        public boolean deleteEmployee(String email) {
        database = dbHelper.getReadableDatabase();
        String whereClause = DbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL + " = ? ";
        String whereArgs[] = new String[1];
        whereArgs[0] = email;
        try {
            if(database.delete(DbContract.EmployeeEntry.TABLE_NAME, whereClause, whereArgs)!=1){
                return false;
            }
            return true;
        }
        catch(Exception e){
            Log.d(TAG,"Exception:",e);
            return false;
        }
    }

    @Override
    public int numberOfRegisteredEmployee() {
        try {
            String employees[] = getAllRegisteredEmployeesName();
            Log.d(TAG, "getAllRegisteredEmployeesName return:" +  (employees.length-1));
            return employees.length-1;  // -1 = -manager

        } catch (Exception e) {
            Log.d(TAG, "numberOfRegisteredEmployee Exception:", e);
        }
        finally {
            if (database != null) {
                database.close();
            }
        }
        return 0;
    }


    @Override
        public List<Employee> getAllEmployee() {
            try {
                database = dbHelper.getReadableDatabase();
                List<Employee> employees = new ArrayList<Employee>();
                String select  = "SELECT * FROM "+ DbContract.EmployeeEntry.TABLE_NAME;

                Cursor cursor =  database.rawQuery(select,null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Employee employee = getEmployeeFromCursor(cursor);
                    employees.add(employee);
                    cursor.moveToNext();
                }
                cursor.close();
                return employees;
            } catch (Exception e) {
                Log.d(TAG, "Exception:", e);
            }
            finally {
                if (database != null) {
                    database.close();
                }
            }
            return null;
        }

    @Override
    public String[] getAllRegisteredEmployeesName() {
        try {
            database = dbHelper.getReadableDatabase();
            List<Employee> employees = new ArrayList<Employee>();
            String select  = "SELECT * FROM "+ DbContract.EmployeeEntry.TABLE_NAME +" WHERE "
                    +DbContract.EmployeeEntry.COLUMN_EMPLOYEE_STATUS + "=?";
            String selectionArgs[] = new String[1];
            selectionArgs[0] = "registered";
            Cursor cursor =  database.rawQuery(select,selectionArgs);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Employee employee = getEmployeeFromCursor(cursor);
                employees.add(employee);
                cursor.moveToNext();
            }
            cursor.close();
            String [] employeesName = new String[employees.size()+1];
            for(int i = 1;i<employees.size()+1;i++){
                employeesName[i] =  employees.get(i-1).getName();
            }
            return employeesName;
        } catch (Exception e) {
            Log.d(TAG, "Exception:", e);
        }
        finally {
            if (database != null) {
                database.close();
            }
        }
        Log.d(TAG, "Return null:");
        return null;
    }

    @Override
    public List<Task> getAllTask(Boolean getPastTask) {
        try {
            database = dbHelper.getReadableDatabase();
            List<Task> tasks = new ArrayList<Task>();
            String select;
            if(getPastTask == true){
                select  = "SELECT * FROM "+ DbContract.TaskEntry.TABLE_NAME;
            }
            else{
                select  = "SELECT * FROM "+ DbContract.TaskEntry.TABLE_NAME + " WHERE ( NOT ("+ DbContract.TaskEntry.COLUMN_STATUS  +"= 'done' ) )";
            }

            Log.d(TAG, select);
            Cursor cursor =  database.rawQuery(select,null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Task task = getTaskFromCursor(cursor);
                tasks.add(task);
                cursor.moveToNext();
            }
            cursor.close();
            return tasks;
        } catch (Exception e) {
            Log.d(TAG, "Exception:", e);
        }
        finally {
            if (database != null) {
                database.close();
            }
        }
        return null;
    }

    private Task getTaskFromCursor(Cursor cursor) {
        String taskDescription = cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_DESCRIPTION));
        String employee =  cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_EMPLOYEE));
        String deadlineStr = cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_DEADLINE));
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        Date deadline = null;
        try {
            deadline = dateFormat.parse(deadlineStr);
        } catch (Exception e) {
            Log.d(TAG,"Parse date exception.Parse string:"+deadlineStr,e);
        }
        String taskHeader = cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_HEADER_TASK));
        String status = cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_STATUS));
        String category = cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_CATEGORY));
        String location = cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_LOCATION));
        int temp =  cursor.getInt(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_PHOTO_REQUIRE));
        boolean photoRequire;
        if(temp==0) {
          photoRequire = false;
        }
        else {
          photoRequire = true;
        }
        String parseId  = cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_TASK_ID));

        return new Task(taskHeader,taskDescription,employee,deadline,status,category,location, photoRequire,parseId,deadlineStr);
    }



    @Override
    public boolean insertTask(Task task) {
        ContentValues content = new ContentValues();
        content.put(DbContract.TaskEntry.COLUMN_HEADER_TASK,task.getTaskHeader());
        content.put(DbContract.TaskEntry.COLUMN_CATEGORY,task.getCategory());
        content.put(DbContract.TaskEntry.COLUMN_EMPLOYEE, task.getEmployee());
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        String deadlineStr = dateFormat.format(task.getDeadline());
        content.put(DbContract.TaskEntry.COLUMN_DEADLINE, deadlineStr);
        content.put(DbContract.TaskEntry.COLUMN_DESCRIPTION, task.getTaskDescription());
        content.put(DbContract.TaskEntry.COLUMN_LOCATION,task.getLocation());
        content.put(DbContract.TaskEntry.COLUMN_STATUS,task.getStatus());
        content.put(DbContract.TaskEntry.COLUMN_PHOTO_REQUIRE,task.isPhotoRequire());
        content.put(DbContract.TaskEntry.COLUMN_TASK_ID, task.getParseId());
        try {
            database = dbHelper.getReadableDatabase();
            if (database.insert(DbContract.TaskEntry.TABLE_NAME, null, content) == -1) {
                Log.d(TAG, "Task not add to database:");
                return false;
            } else {
                Log.d(TAG, "Task add to database:");
                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception:", e);
            return false;
        } finally {
            if (database != null) {
                database.close();

            }
        }
    }

    @Override
    public Task getTaskById(String parseId) {
        try {
            database = dbHelper.getReadableDatabase();
            List<Task> tasks = new ArrayList<Task>();
            String select;
            String whereArgs[] = new String[1];
            whereArgs[0] = parseId;
            select  = "SELECT * FROM "+ DbContract.TaskEntry.TABLE_NAME + " WHERE "+DbContract.TaskEntry.COLUMN_TASK_ID +"= ?";
            Log.d(TAG, select + " " + parseId);
            Cursor cursor =  database.rawQuery(select,whereArgs);
            cursor.moveToFirst();
            Task task = getTaskFromCursor(cursor);
            cursor.close();
            return task;
        } catch (Exception e) {
            Log.d(TAG, "Exception:", e);
        }
        finally {
            if (database != null) {
                database.close();
            }
        }
        return null;
    }

    private Employee getEmployeeFromCursor(Cursor cursor) {
            String email = cursor.getString(cursor.getColumnIndex(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL));
            String name =  cursor.getString(cursor.getColumnIndex(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_PHONE_NUMBER));
            String status = cursor.getString(cursor.getColumnIndex(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_STATUS));
            int taskCount = cursor.getInt(cursor.getColumnIndex(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_TASK_COUNT));
            return new Employee(name,email,phoneNumber,status,taskCount);
        }


    }


