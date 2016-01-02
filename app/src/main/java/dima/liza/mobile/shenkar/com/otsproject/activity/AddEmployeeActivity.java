package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.R;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.AdapterEmployeeToAdd;
import dima.liza.mobile.shenkar.com.otsproject.employee.data.EmployeeToAdd;

public class AddEmployeeActivity extends AppCompatActivity {
    EditText editTextEmail,editTextPhone;
    ListView listView;
    List<EmployeeToAdd> listEmployeeToAdd;
    ListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        editTextEmail = (EditText) findViewById(R.id.addEmployeeEmail);
        editTextPhone = (EditText) findViewById(R.id.addEmployeePhone);
        listEmployeeToAdd = new ArrayList();
        adapter =  new AdapterEmployeeToAdd(this,listEmployeeToAdd);
        listView = (ListView) findViewById(R.id.listViewAddEmployee);
        listView.setAdapter(adapter);
   //     listView.setAdapter();
    }

    public void onClickAddEmployeeSubmit(View view) {

    }

    public void onClickAddToList(View view) {
        String emailToAdd = editTextEmail.getText().toString();
        String phoneToAdd =  editTextPhone.getText().toString();
        //  todo validation
        EmployeeToAdd newEmployee = new EmployeeToAdd(emailToAdd,phoneToAdd);
       listEmployeeToAdd.add(newEmployee);
    //    adapter.notifyDataSetChanged();
     // adapter.notifyAll();
    }
}
