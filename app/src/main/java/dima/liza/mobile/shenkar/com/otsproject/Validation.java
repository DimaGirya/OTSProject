package dima.liza.mobile.shenkar.com.otsproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import dima.liza.mobile.shenkar.com.otsproject.activity.SignInActivity;

/**
 * Created by Girya on 2/23/2016.
 */
public  class Validation {

    public static boolean emailValidation(String email, Context act) {
        if (email == null) {
            Toast.makeText(act, R.string.enterEmail, Toast.LENGTH_LONG).show();
            return false;
        }
        if (!(email.matches("^\\w+[-\\w\\.]*\\@\\w+((-\\w+)|(\\w*))\\.[a-z]{2,3}$"))) {
            Toast.makeText(act, R.string.enterValidEmail, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean passwordValidation(String password, Context act) {
        if (password == null) {
            Toast.makeText(act, R.string.enterPassword, Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.length() < 5) {
            Toast.makeText(act, R.string.passwordMin, Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.length() > 14) {
            Toast.makeText(act, R.string.passwordMax, Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.matches("\\s?.*\\s.*")) {
            Toast.makeText(act, R.string.passwordWhitespace, Toast.LENGTH_LONG).show();
            return false;
        }
        if (!(password.matches("[a-zA-Z0-9@*#]{6,15}"))) {
            Toast.makeText(act, R.string.passwordValidation, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean phoneNumberValidation(String phoneNumber, Context act) {
        if (phoneNumber == null) {
            Toast.makeText(act, R.string.enterPhoneNumber, Toast.LENGTH_LONG).show();
            return false;
        }
        if (phoneNumber.length() > 10 | phoneNumber.length() < 8) {
            Toast.makeText(act, R.string.phoneNumberLength, Toast.LENGTH_LONG).show();
            return false;
        }
        if ((phoneNumber.matches("[0-9]"))) {
            Toast.makeText(act, R.string.phoneNumberValidation, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        Toast.makeText(context, R.string.noInternetToast, Toast.LENGTH_LONG).show();
        return false;
    }

    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    public static String dateToString(Date deadline) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(deadline);
        return cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR)
                + " " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE);
    }
}