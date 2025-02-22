package com.markp.service;

import com.markp.dto.EmployeeDto;
import com.markp.model.Employee;

import java.util.List;

public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto getEmployeeById(Long employeeId);

    List<EmployeeDto> getAllEmployees();

    EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee);

    void deleteEmployee(Long employeeId);

    EmployeeDto assignRoleToEmployee(Long employeeId, Long roleId);

    EmployeeDto getEmployeeByEmail(String email);

    EmployeeDto updateEmployeeByEmail(String email, EmployeeDto updatedEmployee);
}