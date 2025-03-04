package com.markp.impl;

import com.markp.dto.EmployeeDto;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
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
    @CachePut(value = "employees", key = "#result.id")
    public EmployeeDto createEmployee(EmployeeDto employeeDto, String createdBy) {
        if (employeeRepository.existsByEmailAndDeletedFalse(employeeDto.getEmail())) {
            throw new ResourceNotFoundException("Email already exists: " + employeeDto.getEmail());
        }
        Employee employee = employeeMapper.toEntity(employeeDto);
        employee.setCreatedBy(createdBy);
        employee.setUpdatedBy(createdBy);
        employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    @Cacheable(value = "employees", key = "#employeeId")
    public EmployeeDto getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findByActive(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee does not exist with given id: " + employeeId));
        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public Page<EmployeeDto> getAllEmployees(int page, int size,
                                             String firstName, String lastName,
                                             String email, String employmentStatus,
                                             LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
                                             LocalDateTime updatedDateStart, LocalDateTime updatedDateEnd) {
        PageRequest pageRequest = PageRequest.of(page, size);

        // fetch non-encrypted filters
        Page<Employee> employeesPage = employeeRepository.findAllWithNonEncryptedFilters(
                email, employmentStatus, createdDateStart, createdDateEnd, updatedDateStart, updatedDateEnd, pageRequest);

        List<Employee> employees = employeesPage.getContent();

        // filter in memory for encrypted fields
        List<Employee> filteredEmployees = employees.stream()
                .filter(employee -> (firstName == null || employee.getFirstName().toLowerCase().contains(firstName.toLowerCase())))
                .filter(employee -> (lastName == null || employee.getLastName().toLowerCase().contains(lastName.toLowerCase())))
                .collect(Collectors.toList());

        int start = Math.min((int) pageRequest.getOffset(), filteredEmployees.size());
        int end = Math.min((start + pageRequest.getPageSize()), filteredEmployees.size());
        List<EmployeeDto> employeeDtos = filteredEmployees.subList(start, end).stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(employeeDtos, pageRequest, filteredEmployees.size());
    }

    @Override
    @Transactional
    @LogExecutionTime
    @CachePut(value = "employees", key = "#employeeId")
    public EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee, String updatedBy) {
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
        employee.setUpdatedBy(updatedBy);
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
    @CacheEvict(value = "employees", key = "#employeeId")
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
    @CachePut(value = "employees", key = "#employeeId")
    public EmployeeDto assignRoleToEmployee(Long employeeId, Long roleId, String updatedBy) {
        Employee employee = employeeRepository.findByActive(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee does not exist with given id: " + employeeId));
        Role role = roleRepository.findByActive(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role does not exist with given id: " + roleId));
        if (employee.getRoles().stream().noneMatch(r -> r.getId().equals(roleId))) {
            employee.getRoles().add(role);
        }
        employee.setUpdatedBy(updatedBy);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(updatedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    @Cacheable(value = "employees", key = "#email")
    public EmployeeDto getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmailAndDeletedFalse(email);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee does not exist with given email: " + email);
        }
        return employeeMapper.toDto(employee);
    }
}