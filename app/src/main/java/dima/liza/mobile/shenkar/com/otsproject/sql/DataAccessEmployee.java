package dima.liza.mobile.shenkar.com.otsproject.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.employee.data.Employee;

/**
 * Created by Girya on 01/03/2016.
 */

    public class DataAccessEmployee implements iDataAccessEmployee {

        private static final String TAG = "SQL_DATA_ACCESS";
        SQLiteDatabase database;
        private static DataAccessEmployee instance;
        private Context context;
        private DBHelper dbHelper;


        private DataAccessEmployee(Context context) {	//private constructor(singleton)
            try {
                this.context = context;
                dbHelper = new DBHelper(this.context);
            }
            catch (Exception e){
                Log.d(TAG, "Exception:", e);
            }
        }


        public static DataAccessEmployee getInstatnce(Context context) {
            if (instance == null)
                instance = new DataAccessEmployee(context);
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
                Log.e(TAG, "Exception:", e);
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
            values.put(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_STATUS, employee.getStatus());
            database = dbHelper.getReadableDatabase();
            try {
                if(database.update(DbContract.EmployeeEntry.TABLE_NAME, values, whereClause, whereArgs)!=1){
                    return false;
                }
                return true;
            }
            catch(Exception e){
                Log.e(TAG, "Exception:", e);
                return false;
            }
        }

        @Override
        public boolean updateEmployeeTaskCounter(Employee employee, int counter) {
            ContentValues values = new ContentValues();
            String whereClause = DbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL + " = ? ";
            String whereArgs[] = new String[1];
            whereArgs[0] = employee.getEmail();
            values.put(DbContract.EmployeeEntry.COLUMN_EMPLOYEE_TASK_COUNT, employee.getTaskCount());
            database = dbHelper.getReadableDatabase();
            try {
                if(database.update(DbContract.EmployeeEntry.TABLE_NAME, values, whereClause, whereArgs)!=1){
                    return false;
                }
                return true;
            }
            catch(Exception e){
                Log.e(TAG, "Exception:", e);
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
                Log.e(TAG,"Exception:",e);
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
            Log.e(TAG,"Exception:",e);
            return false;
        }
    }


        @Override
        public List<Employee> getAllEmployee() {
            try {
                database = dbHelper.getReadableDatabase();
                List<Employee> studentGrades = new ArrayList<Employee>();
                String select  = "SELECT * FROM "+ DbContract.EmployeeEntry.TABLE_NAME;

                Cursor cursor =  database.rawQuery(select,null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Employee employee = getEmployeeFromCursor(cursor);
                    studentGrades.add(employee);
                    cursor.moveToNext();
                }
                cursor.close();
                return studentGrades;
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


