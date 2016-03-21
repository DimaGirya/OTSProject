package dima.liza.mobile.shenkar.com.otsproject.task.data;

import android.widget.TextView;

/**
 * Created by Girya on 09/03/2016.
 */
public class ViewRowTask {
    TextView taskHeader;
    TextView categoryOrEmployee;
    TextView deadline;
    TextView status;
    TextView taskId;

    public TextView getTaskId() {
        return taskId;
    }

    public void setTaskId(TextView taskId) {
        this.taskId = taskId;
    }

    public ViewRowTask(TextView taskHeader, TextView categoryOrEmployee,  TextView deadline,TextView status,TextView taskId) {
        this.taskHeader = taskHeader;
        this.categoryOrEmployee = categoryOrEmployee;
        this.status = status;
        this.deadline = deadline;
        this.taskId = taskId;
    }
}