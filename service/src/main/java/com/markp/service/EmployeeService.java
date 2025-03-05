package com.markp.service;

import com.markp.dto.EmployeeDto;
import com.markp.dto.request.EmployeeFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto getEmployeeById(Long employeeId);

    Page<EmployeeDto> getAllEmployees(EmployeeFilterRequest filterRequest, Pageable pageable);

    EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee);

    void deleteEmployee(Long employeeId);

    EmployeeDto assignRoleToEmployee(Long employeeId, Long roleId);

    EmployeeDto getEmployeeByEmail(String email);
}