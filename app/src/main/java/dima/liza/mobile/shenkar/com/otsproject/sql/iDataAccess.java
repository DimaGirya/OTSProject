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
     List<Employee> getAllEmployee();
    String[] getAllRegisteredEmployeesName();

    List<Task> getAllTask(Boolean getPastTask);
    boolean insertTask(Task task);
}
