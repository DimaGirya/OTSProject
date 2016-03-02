package dima.liza.mobile.shenkar.com.otsproject.employee.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.R;

/**
 * Created by Girya on 01/03/2016.
 */

    public class AdapterEmployee  extends BaseAdapter {

        private Context context;
        List<Employee> listEmployee;

        public AdapterEmployee(Context context, List<Employee> listEmployee) {
            this.context = context;
            this.listEmployee = listEmployee;
        }

        @Override
        public int getCount() {
            return listEmployee.size();
        }

        @Override
        public Object getItem(int position) {
            return listEmployee.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewRowEmployee viewRowEmployee;
            if(convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.list_of_employee_row, null);
                TextView nameEmployee = (TextView)convertView.findViewById(R.id.listOfEmployeeName);
                TextView numberOfTask = (TextView)convertView.findViewById(R.id.listOfEmployeeNumberOfTask);
                TextView status = (TextView)convertView.findViewById(R.id.listOfEmployeeStatus);
                viewRowEmployee = new ViewRowEmployee(nameEmployee,numberOfTask,status);
                convertView.setTag(viewRowEmployee);
            }
            else
            {
                viewRowEmployee = (ViewRowEmployee) convertView.getTag();
            }
            viewRowEmployee.nameEmployee.setText(listEmployee.get(position).getName());
            //  viewRowEmployee.numberOfTasks.setText(listEmployee.get(position).getTaskCount());
            viewRowEmployee.numberOfTasks.setText(String.valueOf(listEmployee.get(position).getTaskCount())); // new employee. number of task is 0
            viewRowEmployee.status.setText(listEmployee.get(position).getStatus());
            return convertView;
        }
}
