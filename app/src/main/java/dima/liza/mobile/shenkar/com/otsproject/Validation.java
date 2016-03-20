package dima.liza.mobile.shenkar.com.otsproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;

import dima.liza.mobile.shenkar.com.otsproject.activity.SignInActivity;

/**
 * Created by Girya on 2/23/2016.
 */
public  class Validation {

    public static boolean emailValidation(String email,Context act){
        if(email == null) {
            Toast.makeText(act, "Please enter an e-mail", Toast.LENGTH_LONG).show();
            return false;
        }
        if(!(email.matches("^\\w+[-\\w\\.]*\\@\\w+((-\\w+)|(\\w*))\\.[a-z]{2,3}$"))) {
            Toast.makeText(act, "Please enter a valid email address", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public static boolean passwordValidation(String password,Context act){
        if(password == null) {
            Toast.makeText(act, "Please enter a password", Toast.LENGTH_LONG).show();
            return false;
        }
        if(password.length()<5) {
            Toast.makeText(act, "Password must contain at least 6 characters", Toast.LENGTH_LONG).show();
            return false;
        }
        if(password.length()>14) {
            Toast.makeText(act, "Password must contain no more then 15 characters", Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.matches("\\s?.*\\s.*")){
            Toast.makeText(act, "Password must not contain whitespaces", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!(password.matches("[a-zA-Z0-9@*#]{6,15}"))) {
            Toast.makeText(act, "Password can only contain Letters, numbers or '*', '#' and '@'", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public static boolean phoneNumberValidation(String phoneNumber,Context act){
        if(phoneNumber == null) {
            Toast.makeText(act, "Please enter a phone number", Toast.LENGTH_LONG).show();
            return false;
        }
        if (phoneNumber.length() > 10 | phoneNumber.length() < 8) {
            Toast.makeText(act, "Phone number must be beetween 8 to 10 digits", Toast.LENGTH_LONG).show();
            return false;
        }
        if ((phoneNumber.matches("[0-9]"))){
            Toast.makeText(act,"Phone number must contain only digits", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        Toast.makeText(context,"No internet connection. Application work offline", Toast.LENGTH_LONG).show();
        return false;
    }

    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

}
