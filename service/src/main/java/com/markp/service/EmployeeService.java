package com.markp.service;

import com.markp.dto.EmployeeDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto getEmployeeById(Long employeeId);

    Page<EmployeeDto> getAllEmployees(int page, int size,
                                      String firstName, String lastName,
                                      String email, String employmentStatus,
                                      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
                                      LocalDateTime updatedDateStart, LocalDateTime updatedDateEnd);

    EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee);

    void deleteEmployee(Long employeeId);

    EmployeeDto assignRoleToEmployee(Long employeeId, Long roleId);

    EmployeeDto getEmployeeByEmail(String email);
}