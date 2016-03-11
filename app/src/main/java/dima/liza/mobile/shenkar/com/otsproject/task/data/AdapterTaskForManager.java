package dima.liza.mobile.shenkar.com.otsproject.task.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.ViewRowEmployee;
import dima.liza.mobile.shenkar.com.otsproject.task.data.Task;
import dima.liza.mobile.shenkar.com.otsproject.task.data.ViewRowTask;

/**
 * Created by Girya on 09/03/2016.
 */
public class AdapterTaskForManager extends BaseAdapter {

    private Context context;
    List<Task> listOfTask;

    public AdapterTaskForManager(Context context, List<Task> listEmployee) {
        this.context = context;
        this.listOfTask = listEmployee;
    }

    @Override
    public int getCount() {
        return listOfTask.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfTask.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewRowTask viewRowTask;
        if(convertView==null){
           convertView = LayoutInflater.from(context).inflate(R.layout.list_of_task_row, null);
            TextView taskDescription = (TextView)convertView.findViewById(R.id.taskDescription);
            TextView taskEmployee = (TextView)convertView.findViewById(R.id.taskCategoryOrEmployee);
            TextView taskDeadline = (TextView)convertView.findViewById(R.id.taskDeadline);
            TextView taskStatus = (TextView)convertView.findViewById(R.id.taskStatus);
            viewRowTask = new ViewRowTask(taskDescription,taskEmployee,taskDeadline,taskStatus);
           convertView.setTag(viewRowTask);
        }
        else
        {
            viewRowTask = (ViewRowTask) convertView.getTag();
        }
        viewRowTask.taskDescription.setText(listOfTask.get(position).getTaskDescription());
        viewRowTask.categoryOrEmployee.setText(listOfTask.get(position).getEmployee());
        viewRowTask.deadline.setText(String.valueOf(listOfTask.get(position).getDeadline())); // new employee. number of task is 0
        viewRowTask.status.setText(listOfTask.get(position).getStatus());
        return convertView;
    }
}
