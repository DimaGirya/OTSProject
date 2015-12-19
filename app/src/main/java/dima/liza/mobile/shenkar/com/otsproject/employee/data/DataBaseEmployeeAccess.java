package dima.liza.mobile.shenkar.com.otsproject.employee.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girya on 12/19/2015.
 */
public class DataBaseEmployeeAccess implements IDataEmployee {
    private static DataBaseEmployeeAccess ourInstance;
    private Context context;
    private EmployeeDBHelper dbHelper;
    private String[] friendsColumns = { EmployeeDbContract.EmployeeEntry._ID,
            EmployeeDbContract.EmployeeEntry.COLUMN_EMPLOYEE_NAME,
            EmployeeDbContract.EmployeeEntry.COLUMN_EMPLOYEE_PHONE_NUMBER,
            EmployeeDbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL };
    public static DataBaseEmployeeAccess getInstance(Context context)
    {
        if (ourInstance == null)
            ourInstance = new DataBaseEmployeeAccess(context);
        return ourInstance;
    }

    private DataBaseEmployeeAccess(Context context) {
        this.context = context;
        dbHelper = new EmployeeDBHelper(this.context);
    }

    @Override
    public Employee addEmployee(Employee employee) {
        SQLiteDatabase database = null;
        try {
            database = dbHelper.getReadableDatabase();
            if (employee == null)
                return null;

            ContentValues values = new ContentValues();
            values.put(EmployeeDbContract.EmployeeEntry.COLUMN_EMPLOYEE_NAME, employee.getName());
            values.put(EmployeeDbContract.EmployeeEntry.COLUMN_EMPLOYEE_PHONE_NUMBER, employee.getPhoneNumber());
            values.put(EmployeeDbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL, employee.getEmail());

            long insertId = database.insert(EmployeeDbContract.EmployeeEntry.TABLE_NAME, null, values);


            Cursor cursor = database.query(EmployeeDbContract.EmployeeEntry.TABLE_NAME, friendsColumns,
                    EmployeeDbContract.EmployeeEntry._ID + " = " + insertId, null, null, null, null);
            cursor.moveToFirst();

            Employee newEmployee = cursorToEmployee(cursor);
            cursor.close();
            return newEmployee;

        }finally {
            if (database != null)
                database.close();
        }
    }
    private Employee cursorToEmployee(Cursor cursor) {
        Employee employee = new Employee();
        employee.setId(cursor.getInt(cursor.getColumnIndex(EmployeeDbContract.EmployeeEntry._ID)));
        employee.setName(cursor.getString(cursor.getColumnIndex(EmployeeDbContract.EmployeeEntry.COLUMN_EMPLOYEE_NAME)));
        employee.setPhoneNumber(cursor.getString(cursor.getColumnIndex(EmployeeDbContract.EmployeeEntry.COLUMN_EMPLOYEE_PHONE_NUMBER)));
        employee.setEmail(cursor.getString(cursor.getColumnIndex(EmployeeDbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL)));
        return employee;
    }
    @Override
    public boolean removeEmployee(Employee employee) {
        SQLiteDatabase database = null;
        try {
            database = dbHelper.getReadableDatabase();
            long id = employee.getId();
            database.delete(EmployeeDbContract.EmployeeEntry.TABLE_NAME,EmployeeDbContract.EmployeeEntry._ID + " = " + id, null);
        }finally {
            if(database != null){
                database.close();
            }
        }
        return true;
    }

    @Override
    public List<Employee> getEmployeeList() {
        SQLiteDatabase database = null;
        try {
            database = dbHelper.getReadableDatabase();
            List<Employee> friends = new ArrayList<Employee>();

            Cursor cursor = database.query(EmployeeDbContract.EmployeeEntry.TABLE_NAME, friendsColumns,
                    null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Employee employee = cursorToEmployee(cursor);
                friends.add(employee);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            return friends;
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }

    @Override
    public Employee getEmployeeByName() {
        return null;
    }
}
