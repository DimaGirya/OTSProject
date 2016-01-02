package dima.liza.mobile.shenkar.com.otsproject.employee.data;

/**
 * Created by Girya on 12/30/2015.
 */
public class EmployeeToAdd {
    private String email;
    private String phone;

    public EmployeeToAdd(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
