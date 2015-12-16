package dima.liza.mobile.shenkar.com.otsproject;

/**
 * Created by Girya on 12/16/2015.
 */
public  class AuthorizationLocal implements IAuthorization{
    @Override
    public  boolean signUp(String email, String password, String phoneNumber) {
        return true;
    }

    @Override
    public boolean signIn(String email, String password, String phoneNumber) {
        return true;
    }
}
