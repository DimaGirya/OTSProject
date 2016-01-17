package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

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
    int numOfNewEmployee;
    int numOfAddNewEmployee;
    private static String TAG  = "AddEmployeeActivity";
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
        ProgressDialog  pd = new ProgressDialog(this);
        pd.setTitle("Adding employee to data base");
        pd.setMessage("Please wait");
        pd.show();
        ParseUser currentUser = ParseUser.getCurrentUser();
        EmployeeToAdd employeeToAdd;
         numOfNewEmployee = listEmployeeToAdd.size();
         numOfAddNewEmployee = 0;
        for (int i = 0; i < listEmployeeToAdd.size(); i++) {
            ParseObject newEmployee = new ParseObject("newEmployee");
            employeeToAdd = listEmployeeToAdd.get(i);
            newEmployee.put("email", employeeToAdd.getEmail());
            newEmployee.put("numberPhone", employeeToAdd.getPhone());
            newEmployee.put("manager", currentUser.getEmail());
            newEmployee.put("isManager", false);
            newEmployee.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {    // SIGN UP DONE
                        numOfAddNewEmployee++;
                        // todo send email to user
                        Toast.makeText(AddEmployeeActivity.this, "Add new employee done", Toast.LENGTH_LONG).show();
                        if (numOfAddNewEmployee == numOfNewEmployee) {
                            // todo notificate user add done
                            Toast.makeText(AddEmployeeActivity.this, "Add all new employee done", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    } else {
                        Toast.makeText(AddEmployeeActivity.this, "Add new employee fail", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "ParseException:", e);
                        // push notification to user that add fail
                    }
                }
            });

        }

    }

    public void onClickAddToList(View view) {
        String emailToAdd = editTextEmail.getText().toString();
        String phoneToAdd =  editTextPhone.getText().toString();
        //  todo validation
        EmployeeToAdd newEmployee = new EmployeeToAdd(emailToAdd,phoneToAdd);
       listEmployeeToAdd.add(newEmployee);
    //    adapter.notifyDataSetChanged();
     // adapter.notifyAll();

        // TODO clean after adding and update table
    }
}
