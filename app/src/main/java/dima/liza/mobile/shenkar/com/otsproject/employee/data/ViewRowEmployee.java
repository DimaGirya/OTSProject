package dima.liza.mobile.shenkar.com.otsproject.employee.data;

import android.widget.TextView;

/**
 * Created by Girya on 01/03/2016.
 */

    public class ViewRowEmployee {
    TextView nameEmployee;
    TextView numberOfTasks;
    TextView status;

    public ViewRowEmployee(TextView nameEmployee, TextView numberOfTasks, TextView status) {
        this.nameEmployee = nameEmployee;
        this.numberOfTasks = numberOfTasks;
        this.status = status;
    }
}

