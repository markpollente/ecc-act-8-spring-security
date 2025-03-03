package com.markp.service;

import com.markp.dto.EmployeeDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeDto employeeDto, String createdBy);

    EmployeeDto getEmployeeById(Long employeeId);

    Page<EmployeeDto> getAllEmployees(int page, int size,
                                      String firstName, String lastName,
                                      String email, String employmentStatus,
                                      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
                                      LocalDateTime updatedDateStart, LocalDateTime updatedDateEnd);

    EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee, String updatedBy);

    void deleteEmployee(Long employeeId);

    EmployeeDto assignRoleToEmployee(Long employeeId, Long roleId, String updatedBy);

    EmployeeDto getEmployeeByEmail(String email);
}