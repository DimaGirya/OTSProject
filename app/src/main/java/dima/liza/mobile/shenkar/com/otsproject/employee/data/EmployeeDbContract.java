package dima.liza.mobile.shenkar.com.otsproject.employee.data;

import android.provider.BaseColumns;

/**
 * Created by Girya on 12/19/2015.
 */
public class EmployeeDbContract {
    /* Inner class that defines the table contents of the friends */
    public static final class  EmployeeEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "employees";

        public static final String COLUMN_EMPLOYEE_NAME = "employees_name";

        public static final String COLUMN_EMPLOYEE_PHONE_NUMBER = "employees_phone_number";

        public static final String COLUMN_EMPLOYEE_EMAIL = "employees_email";
    }
}
/*
 String name;
    String email;
    String phone;
 */