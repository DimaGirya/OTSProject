package dima.liza.mobile.shenkar.com.otsproject.task.data;

import java.util.Date;

/**
 * Created by Girya on 09/03/2016.
 */
public class Task {
    private String taskDescription;
    private String employee;
    private Date deadline;
    private String status;
    private String category;
    private String location;
    private String parseId;
    private boolean photoRequire;

    public Task(String taskDescription, String employee, Date deadline, String status, String category, String location, boolean photoRequire,String parseId) {
        this.taskDescription = taskDescription;
        this.employee = employee;
        this.deadline = deadline;
        this.status = status;
        this.category = category;
        this.location = location;
        this.photoRequire = photoRequire;
    }

    public String getParseId() {
        return parseId;
    }

    public void setParseId(String parseId) {
        this.parseId = parseId;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isPhotoRequire() {
        return photoRequire;
    }

    public void setPhotoRequire(boolean photoRequire) {
        this.photoRequire = photoRequire;
    }


}
