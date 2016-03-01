package dima.liza.mobile.shenkar.com.otsproject.employee.data;

/**
 * Created by Girya on 12/19/2015.
 */
public class Employee {
    private String name;
    private String email;
    private String phoneNumber;
    private String status;
    private int taskCount;

    public Employee(String name, String email, String phoneNumber, String status, int taskCount) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.taskCount = taskCount;
    }

    public Employee(EmployeeToAdd employeeToAdd){
        name = email = employeeToAdd.getEmail();
        phoneNumber = employeeToAdd.getPhone();
        status = "Not sign up";
        taskCount = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public void incrementTaskCount(){
        taskCount++;
    }
    public void decrementTaskCount(){
        taskCount--;
    }
}
