package dima.liza.mobile.shenkar.com.otsproject;

import android.content.Context;

/**
 * Created by Girya on 12/16/2015.
 */
public interface IAuthorization {
    public boolean signUp(String email,String password,String phoneNumber, Context act);
    public boolean signIn(String email,String password,String phoneNumber);
}
