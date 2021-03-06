package dima.liza.mobile.shenkar.com.otsproject.sql;

import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.employee.data.Employee;
import dima.liza.mobile.shenkar.com.otsproject.task.data.Task;

/**
 * Created by Girya on 01/03/2016.
 */
public interface iDataAccess {
    boolean insertEmployee(Employee employee);
    boolean updateEmployeeStatus(Employee employee,String status);
    boolean updateEmployeeTaskCounter(Employee employee,int counter);
    boolean deleteEmployee(Employee employee);
    boolean deleteEmployee(String email);
    int numberOfRegisteredEmployee();
     List<Employee> getAllEmployee();
    String[] getAllRegisteredEmployeesName();
    //added by liza
    String[] getLocations();

    List<Task> getAllTask(Boolean getPastTask);
    boolean insertTask(Task task);
    boolean insertLocations(String[] locationsFromParse);

    Task getTaskById(String parseId);
    int getNumberOfTask(Boolean getPastTask);
    boolean updateTask(Task task);
}
