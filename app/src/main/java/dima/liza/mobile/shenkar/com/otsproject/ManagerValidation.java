package dima.liza.mobile.shenkar.com.otsproject;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import dima.liza.mobile.shenkar.com.otsproject.activity.AddTaskActivity;
import dima.liza.mobile.shenkar.com.otsproject.activity.ShowTaskManagerActivity;
import dima.liza.mobile.shenkar.com.otsproject.activity.SignUpManagerActivity;
import dima.liza.mobile.shenkar.com.otsproject.sql.DataAccess;


/**
 * Created by Girya on 12/16/2015.
 */
public class ManagerValidation {

    public   boolean signUpValidation(String email, String password, String phoneNumber, Context act) {
        //TO DO Liza - Along with password, add whitespaces validation to all other fields
        if(!Validation.emailValidation(email,act)) {
            return false;
        }
        if(!Validation.passwordValidation(password, act)){
            return false;
        }
        if(!Validation.phoneNumberValidation(phoneNumber, act)){
            return false;
        }
        return true;
    }


    public boolean signIn(String email, String password, String phoneNumber) {

        return true;
    }

    public static void checkRegisteredEmployee(Context context,DataAccess dataAccess) {
        if(dataAccess.numberOfRegisteredEmployee()>0) {
            Intent intent = new Intent(context, AddTaskActivity.class);
            context.startActivity(intent);
        }
        else{
            Toast.makeText(context, R.string.youDontHaveWorkers,Toast.LENGTH_LONG).show();
        }
    }
}
