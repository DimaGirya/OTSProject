package dima.liza.mobile.shenkar.com.otsproject.sql;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Girya on 01/03/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "otsProject.db";
    private static final String TAG = "DBHelperLog";
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            final String SQL_CREATE_EMPLOYEE_TABLE = "CREATE TABLE "
                    + DbContract.EmployeeEntry.TABLE_NAME + " (" + DbContract.EmployeeEntry._ID+ " INTEGER PRIMARY KEY,"
                    + DbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL +" UNIQUE ON CONFLICT REPLACE," + DbContract.EmployeeEntry.COLUMN_EMPLOYEE_NAME
                    + "," + DbContract.EmployeeEntry.COLUMN_EMPLOYEE_PHONE_NUMBER +"," +DbContract.EmployeeEntry.COLUMN_EMPLOYEE_TASK_COUNT
                    + "," + DbContract.EmployeeEntry.COLUMN_EMPLOYEE_STATUS
                    + " TEXT NOT NULL)";
            final String SQL_CREATE_TASK_TABLE = "CREATE TABLE "
                    +DbContract.TaskEntry.TABLE_NAME + " (" + DbContract.TaskEntry._ID+ " INTEGER PRIMARY KEY,"
                    + DbContract.TaskEntry.COLUMN_TASK_ID  +" UNIQUE ON CONFLICT REPLACE," + DbContract.TaskEntry.COLUMN_DESCRIPTION +","
                    + DbContract.TaskEntry.COLUMN_EMPLOYEE + "," + DbContract.TaskEntry.COLUMN_CATEGORY + "," +DbContract.TaskEntry.COLUMN_LOCATION
                    + "," + DbContract.TaskEntry.COLUMN_STATUS + "," + DbContract.TaskEntry.COLUMN_DEADLINE + "," + DbContract.TaskEntry.COLUMN_PRIORITY +","
                    + DbContract.TaskEntry.COLUMN_HEADER_TASK +","+ DbContract.TaskEntry.COLUMN_PHOTO_REQUIRE + " INTEGER)";
            Log.i(TAG,SQL_CREATE_EMPLOYEE_TABLE);
            Log.i(TAG,SQL_CREATE_TASK_TABLE);

            db.execSQL(SQL_CREATE_EMPLOYEE_TABLE);
            db.execSQL(SQL_CREATE_TASK_TABLE);

        } catch (SQLException e) {
            Log.d(TAG, "Exception:", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.EmployeeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskEntry.TABLE_NAME);
        onCreate(db);
    }
}
