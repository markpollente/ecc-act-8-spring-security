package com.markp.mapper;

import com.markp.dto.EmployeeDto;
import com.markp.model.Employee;

import java.util.stream.Collectors;

public class EmployeeMapper {

    public static EmployeeDto mapToEmployeeDto(Employee employee) {
        if (employee == null) {
            return null;
        }
        return new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getAge(),
                employee.getAddress(),
                employee.getContactNumber(),
                employee.getEmploymentStatus(),
                employee.getPassword(),
                employee.getRoles() != null ? employee.getRoles().stream()
                        .map(RoleMapper::mapToRoleDto)
                        .collect(Collectors.toList()) : null
        );
    }

    public static Employee mapToEmployee(EmployeeDto employeeDto) {
        if (employeeDto == null) {
            return null;
        }
        return new Employee(
                employeeDto.getId(),
                employeeDto.getFirstName(),
                employeeDto.getLastName(),
                employeeDto.getEmail(),
                employeeDto.getAge(),
                employeeDto.getAddress(),
                employeeDto.getContactNumber(),
                employeeDto.getEmploymentStatus(),
                employeeDto.getPassword(),
                employeeDto.getRoles() != null ? employeeDto.getRoles().stream()
                        .map(RoleMapper::mapToRole)
                        .collect(Collectors.toList()) : null
        );
    }
}
