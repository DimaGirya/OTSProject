package dima.liza.mobile.shenkar.com.otsproject.task.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.Validation;

/**
 * Created by Girya on 09/03/2016.
 */
public class AdapterTaskForManager extends BaseAdapter /*implements  AdapterView.OnItemLongClickListener*/ {
    private static String TAG = "AdapterTaskForManager";
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
            TextView taskDescription = (TextView)convertView.findViewById(R.id.taskHeader);
            TextView taskEmployee = (TextView)convertView.findViewById(R.id.taskCategoryOrEmployee);
            TextView taskDeadline = (TextView)convertView.findViewById(R.id.taskDeadline);
            TextView taskStatus = (TextView)convertView.findViewById(R.id.taskStatus);
            TextView taskId = (TextView)convertView.findViewById(R.id.taskId);
            viewRowTask = new ViewRowTask(taskDescription,taskEmployee,taskDeadline,taskStatus,taskId);
           convertView.setTag(viewRowTask);
        }
        else
        {
            viewRowTask = (ViewRowTask) convertView.getTag();
        }
        viewRowTask.taskHeader.setText(listOfTask.get(position).getTaskHeader());
        viewRowTask.categoryOrEmployee.setText(listOfTask.get(position).getEmployee());
       // viewRowTask.deadline.setText(String.valueOf(listOfTask.get(position).getDeadline()));
        String  temp = Validation.dateToString(listOfTask.get(position).getDeadline());
        viewRowTask.deadline.setText(temp);
        viewRowTask.status.setText(listOfTask.get(position).getStatus());
        viewRowTask.taskId.setText(listOfTask.get(position).getParseId());
        return convertView;
    }






/*
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        View v = (View) view.getParent();
        TextView temp = (TextView)view.findViewById(R.id.taskId);
        String str = temp.getText().toString();
        Log.d(TAG, str);

        return true;
    }
    */
}
