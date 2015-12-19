package dima.liza.mobile.shenkar.com.otsproject.employee.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Girya on 12/19/2015.
 */
public class EmployeeDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "friends.db";

    public EmployeeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE "
                + EmployeeDbContract.EmployeeEntry.TABLE_NAME + " (" + EmployeeDbContract.EmployeeEntry._ID
                + " INTEGER PRIMARY KEY," + EmployeeDbContract.EmployeeEntry.COLUMN_EMPLOYEE_NAME
                + " TEXT NOT NULL, " + EmployeeDbContract.EmployeeEntry.COLUMN_EMPLOYEE_PHONE_NUMBER
                + " TEXT NOT NULL, " + EmployeeDbContract.EmployeeEntry.COLUMN_EMPLOYEE_EMAIL
                + " TEXT NOT NULL  UNIQUE ON CONFLICT REPLACE)";
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EmployeeDbContract.EmployeeEntry.TABLE_NAME);
        onCreate(db);
    }
}
