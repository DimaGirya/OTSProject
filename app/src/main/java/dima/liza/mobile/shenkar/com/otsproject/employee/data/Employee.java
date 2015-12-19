package dima.liza.mobile.shenkar.com.otsproject.employee.data;

/**
 * Created by Girya on 12/19/2015.
 */
public class Employee {
    private String name;
    private String email;
    private String phoneNumber;
    private int id;

    public Employee() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    // String password;
    public Employee(String name, String email, String phoneNumber,int id) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.id = id;

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
