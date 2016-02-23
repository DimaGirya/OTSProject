package dima.liza.mobile.shenkar.com.otsproject;

import android.content.Context;
import android.widget.Toast;
import dima.liza.mobile.shenkar.com.otsproject.activity.SignUpManagerActivity;


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
}
