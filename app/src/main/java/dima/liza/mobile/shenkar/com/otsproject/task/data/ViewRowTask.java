package dima.liza.mobile.shenkar.com.otsproject.task.data;

import android.widget.TextView;

/**
 * Created by Girya on 09/03/2016.
 */
public class ViewRowTask {
    TextView taskDescription;
    TextView categoryOrEmployee;
    TextView deadline;
    TextView status;
    TextView taskId;
    public ViewRowTask(TextView taskDescription, TextView categoryOrEmployee,  TextView deadline,TextView status,TextView taskId) {
        this.taskDescription = taskDescription;
        this.categoryOrEmployee = categoryOrEmployee;
        this.status = status;
        this.deadline = deadline;
        this.taskId = taskId;
    }
}