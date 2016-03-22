package dima.liza.mobile.shenkar.com.otsproject.task.data;

import java.util.Date;

/**
 * Created by Girya on 09/03/2016.
 */
public class Task {
    private String taskHeader;
    private String taskDescription;
    private String employee;
    private Date deadline;
    private String status;
    private String category;
    private String location;
    private String parseId;
    private String deadlineStr;
    private String priority;
    private boolean photoRequire;

    public String getDeadlineStr() {
        return deadlineStr;
    }

    public void setDeadlineStr(String deadlineStr) {
        this.deadlineStr = deadlineStr;
    }

    public Task(String taskHeader,String taskDescription, String employee, Date deadline,String priority, String status, String category, String location, boolean photoRequire,String parseId,String deadlineStr) {
        this.taskHeader = taskHeader;
        this.taskDescription = taskDescription;
        this.employee = employee;
        this.deadline = deadline;
        this.priority = priority;
        this.status = status;
        this.category = category;
        this.location = location;
        this.photoRequire = photoRequire;
        this.parseId = parseId;
        this.deadlineStr = deadlineStr;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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

    public void setTaskHeader(String taskHeader) {
        this.taskHeader = taskHeader;
    }

    public String getTaskHeader() {
        return taskHeader;
    }
}
