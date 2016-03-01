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
                    + DbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL +" UNIQUE," + DbContract.EmployeeEntry.COLUMN_EMPLOYEE_NAME
                    + "," + DbContract.EmployeeEntry.COLUMN_EMPLOYEE_PHONE_NUMBER +"," +DbContract.EmployeeEntry.COLUMN_EMPLOYEE_TASK_COUNT
                    + "," + DbContract.EmployeeEntry.COLUMN_EMPLOYEE_STATUS
                    + " TEXT NOT NULL)";
            db.execSQL(SQL_CREATE_EMPLOYEE_TABLE);


        } catch (SQLException e) {
            Log.d(TAG, "Exception:", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.EmployeeEntry.TABLE_NAME);
        onCreate(db);
    }
}
