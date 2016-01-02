package dima.liza.mobile.shenkar.com.otsproject.employee.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.R;

/**
 * Created by Girya on 1/2/2016.
 */
public class AdapterEmployeeToAdd extends BaseAdapter {

    private Context context;
    private List<EmployeeToAdd> employeeToAdd;


    @Override
    public int getCount() {
        return employeeToAdd.size();
    }

    @Override
    public Object getItem(int position) {
        return employeeToAdd.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewRowAddEmployee viewRowAddEmployee;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_to_add_layout, null);
            TextView emailEmployee = (TextView)convertView.findViewById(R.id.listToAddEmployeeEmail);
            TextView emailPhone = (TextView)convertView.findViewById(R.id.listToAddEmployeePhone);
            viewRowAddEmployee = new ViewRowAddEmployee(emailEmployee,emailPhone);
            convertView.setTag(viewRowAddEmployee);
        }
        else
        {
            viewRowAddEmployee = (ViewRowAddEmployee) convertView.getTag();
        }
        viewRowAddEmployee.emailView.setText(employeeToAdd.get(position).getEmail());
        viewRowAddEmployee.phoneView.setText(employeeToAdd.get(position).getPhone());
        return convertView;
    }

}
