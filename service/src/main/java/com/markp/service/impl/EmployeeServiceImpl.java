package com.markp.service.impl;

import com.markp.dto.EmployeeDto;
import com.markp.dto.request.EmployeeFilterRequest;
import com.markp.exception.ResourceNotFoundException;
import com.markp.logging.LogExecutionTime;
import com.markp.mapper.EmployeeMapper;
import com.markp.mapper.RoleMapper;
import com.markp.model.Employee;
import com.markp.model.HelpdeskTicket;
import com.markp.model.Role;
import com.markp.repository.EmployeeRepository;
import com.markp.repository.HelpdeskTicketRepository;
import com.markp.repository.RoleRepository;
import com.markp.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Validated
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private HelpdeskTicketRepository helpdeskTicketRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    @Transactional
    @LogExecutionTime
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        if (employeeRepository.existsByEmailAndDeletedFalse(employeeDto.getEmail())) {
            throw new ResourceNotFoundException("Email already exists: " + employeeDto.getEmail());
        }
        Employee employee = employeeMapper.toEntity(employeeDto);
        employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public EmployeeDto getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findByActive(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee does not exist with given id: " + employeeId));
        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public Page<EmployeeDto> getAllEmployees(EmployeeFilterRequest filterRequest, Pageable pageable) {

        return employeeRepository
                .findAllWithFilters(filterRequest.getFirstName(), filterRequest.getLastName(), filterRequest.getEmail(),
                        filterRequest.getEmploymentStatus(), filterRequest.getCreatedBy(), filterRequest.getUpdatedBy(),
                        filterRequest.getCreatedDateStart(), filterRequest.getCreatedDateEnd(),
                        filterRequest.getUpdatedDateStart(), filterRequest.getUpdatedDateEnd(), pageable)
                .map(employeeMapper::toDto);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee) {
        Employee employee = employeeRepository.findByActive(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee does not exist with given id: " + employeeId));
        if (!updatedEmployee.getEmail().equals(employee.getEmail()) &&
                employeeRepository.existsByEmailAndDeletedFalse(updatedEmployee.getEmail())) {
            throw new ResourceNotFoundException("Email already exists: " + updatedEmployee.getEmail());
        }
        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        employee.setEmail(updatedEmployee.getEmail());
        employee.setBirthday(updatedEmployee.getBirthday());
        employee.setAge(employee.getAge());
        employee.setAddress(updatedEmployee.getAddress());
        employee.setContactNumber(updatedEmployee.getContactNumber());
        employee.setEmploymentStatus(updatedEmployee.getEmploymentStatus());
        if (updatedEmployee.getPassword() != null && !updatedEmployee.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(updatedEmployee.getPassword()));
        }
        if (updatedEmployee.getRoles() != null) {
            employee.setRoles(updatedEmployee.getRoles().stream()
                    .map(roleMapper::toEntity)
                    .collect(Collectors.toList()));
        }
        Employee updatedEmployeeObj = employeeRepository.save(employee);
        return employeeMapper.toDto(updatedEmployeeObj);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public void deleteEmployee(Long employeeId) {
        List<HelpdeskTicket> tickets = helpdeskTicketRepository.findByAssigneeIdAndDeletedFalse(employeeId);
        for (HelpdeskTicket ticket : tickets) {
            ticket.setAssignee(null);
            helpdeskTicketRepository.save(ticket);
        }
        Employee employee = employeeRepository.findByActive(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee does not exist with given id: " + employeeId));
        employee.setDeleted(true);
        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public EmployeeDto assignRoleToEmployee(Long employeeId, Long roleId) {
        Employee employee = employeeRepository.findByActive(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee does not exist with given id: " + employeeId));
        Role role = roleRepository.findByActive(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role does not exist with given id: " + roleId));
        if (employee.getRoles().stream().noneMatch(r -> r.getId().equals(roleId))) {
            employee.getRoles().add(role);
        }
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(updatedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public EmployeeDto getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmailAndDeletedFalse(email);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee does not exist with given email: " + email);
        }
        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public Page<EmployeeDto> getEmployeeDirectory(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findByDeletedFalse(pageable);
        return employees.map(employee -> {
            EmployeeDto dto = new EmployeeDto();
            dto.setId(employee.getId());
            dto.setFirstName(employee.getFirstName());
            dto.setLastName(employee.getLastName());
            return dto;
        });
    }
}