package com.markp.service;

import com.markp.dto.EmployeeDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeDto employeeDto, String createdBy);

    EmployeeDto getEmployeeById(Long employeeId);

    Page<EmployeeDto> getAllEmployees(int page, int size);

    EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee, String updatedBy);

    void deleteEmployee(Long employeeId);

    EmployeeDto assignRoleToEmployee(Long employeeId, Long roleId, String updatedBy);

    EmployeeDto getEmployeeByEmail(String email);
}