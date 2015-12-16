package dima.liza.mobile.shenkar.com.otsproject;

/**
 * Created by Girya on 12/16/2015.
 */
public interface IAuthorization {
    public boolean signUp(String email,String password,String phoneNumber);
    public boolean signIn(String email,String password,String phoneNumber);
}
