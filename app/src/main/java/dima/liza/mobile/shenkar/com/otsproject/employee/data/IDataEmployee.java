package dima.liza.mobile.shenkar.com.otsproject.employee.data;

import java.util.List;

/**
 * Created by Girya on 12/19/2015.
 */
public interface IDataEmployee {
    public Employee addEmployee(Employee employee);
    public boolean removeEmployee(Employee employee);
    public List<Employee> getEmployeeList();
    public Employee getEmployeeByName();
}
