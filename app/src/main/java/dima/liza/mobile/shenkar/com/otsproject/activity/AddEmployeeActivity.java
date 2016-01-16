package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseUser;
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
        ParseUser currentUser = ParseUser.getCurrentUser();
        for (int i = 0; i < listEmployeeToAdd.size(); i++) {
            ParseUser user = new ParseUser();
            user.setUsername(listEmployeeToAdd.get(i).getEmail());
            user.setPassword(listEmployeeToAdd.get(i).getPhone());
            user.setEmail(listEmployeeToAdd.get(i).getEmail());
            user.put("phoneNumber", listEmployeeToAdd.get(i).getPhone());
            user.put("isManager", false);
            user.put("manager", currentUser.getUsername());
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {    // SIGN UP DONE
                        // todo send email to user
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        Toast.makeText(AddEmployeeActivity.this, currentUser.getUsername(),Toast.LENGTH_LONG).show();
                        Toast.makeText(AddEmployeeActivity.this, "Sign up done", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(AddEmployeeActivity.this,"Sign up fail",Toast.LENGTH_LONG).show();
                        Log.d(TAG, "ParseException:", e);
                    }
                }
            });

        }
        finish(); // not good
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
