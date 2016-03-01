package dima.liza.mobile.shenkar.com.otsproject.sql;

import android.provider.BaseColumns;

/**
 * Created by Girya on 01/03/2016.
 */
public class DbContract {
    public  static final class EmployeeEntry implements BaseColumns {
        public static final String TABLE_NAME = "Employee";
        public static final String COLUMN_EMPLOYEE_NAME = "EmployeeName";
        public static final String COLUMN_EMPLOYEE_EMAIL = "EmployeeEmail";
        public static final String COLUMN_EMPLOYEE_PHONE_NUMBER = "EmployeePhoneNumber";
        public static final String COLUMN_EMPLOYEE_STATUS= "EmployeeStatus";
        public static final String COLUMN_EMPLOYEE_TASK_COUNT= "EmployeeTaskCount";
    }

}
