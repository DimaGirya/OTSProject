package dima.liza.mobile.shenkar.com.otsproject.sql;

import java.util.List;

import dima.liza.mobile.shenkar.com.otsproject.employee.data.Employee;

/**
 * Created by Girya on 01/03/2016.
 */
public interface iDataAccessEmployee {
    boolean insertEmployee(Employee employee);
    boolean updateEmployeeStatus(Employee employee,String status);
    boolean updateEmployeeTaskCounter(Employee employee,int counter);
    boolean deleteEmployee(Employee employee);
     List<Employee> getAllEmployee();
}
