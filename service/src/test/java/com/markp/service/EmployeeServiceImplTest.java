package com.markp.service;

import com.markp.dto.EmployeeDto;
import com.markp.dto.RoleDto;
import com.markp.dto.request.EmployeeFilterRequest;
import com.markp.exception.ResourceNotFoundException;
import com.markp.impl.EmployeeServiceImpl;
import com.markp.mapper.EmployeeMapper;
import com.markp.mapper.RoleMapper;
import com.markp.model.Employee;
import com.markp.model.HelpdeskTicket;
import com.markp.model.Role;
import com.markp.repository.EmployeeRepository;
import com.markp.repository.HelpdeskTicketRepository;
import com.markp.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private HelpdeskTicketRepository helpdeskTicketRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeDto employeeDto;
    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        role = new Role("ADMIN", "Administrator role", new ArrayList<>());
        role.setId(1L); // Ensure the role has a non-null id
        roleDto = new RoleDto("ADMIN", "Administrator role");
        employee = new Employee("Mark", "Pollente", "markp@hmm.com", LocalDate.of(1990, 1, 1), 30, "123 Antipolo St", "0951234678", "Active", "password", new ArrayList<>(Arrays.asList(role)));
        employeeDto = new EmployeeDto("First", "Last", "first.last@gmail.com", LocalDate.of(1990, 1, 1), 30, "123 Antipolo St", "0951234678", "Active", "password", new ArrayList<>(Arrays.asList(roleDto)));
    }

    @Test
    void createEmployee() {
        when(employeeMapper.toEntity(any(EmployeeDto.class))).thenReturn(employee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toDto(any(Employee.class))).thenReturn(employeeDto);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");

        EmployeeDto savedEmployee = employeeService.createEmployee(employeeDto);

        assertNotNull(savedEmployee);
        assertEquals(employeeDto.getFirstName(), savedEmployee.getFirstName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void getEmployeeById() {
        when(employeeRepository.findByActive(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDto(any(Employee.class))).thenReturn(employeeDto);

        EmployeeDto foundEmployee = employeeService.getEmployeeById(1L);

        assertNotNull(foundEmployee);
        assertEquals(employeeDto.getFirstName(), foundEmployee.getFirstName());
        verify(employeeRepository, times(1)).findByActive(1L);
    }

    @Test
    void getAllEmployees() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> employeePage = new PageImpl<>(Arrays.asList(employee));
        when(employeeRepository.findAllWithFilters(any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(employeePage);

        Page<EmployeeDto> employees = employeeService.getAllEmployees(new EmployeeFilterRequest(), pageable);

        assertNotNull(employees);
        assertEquals(1, employees.getTotalElements());
        verify(employeeRepository, times(1)).findAllWithFilters(any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void updateEmployee() {
        when(employeeRepository.findByActive(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toDto(any(Employee.class))).thenReturn(employeeDto);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");

        EmployeeDto updatedEmployee = employeeService.updateEmployee(1L, employeeDto);

        assertNotNull(updatedEmployee);
        assertEquals(employeeDto.getFirstName(), updatedEmployee.getFirstName());
        verify(employeeRepository, times(1)).findByActive(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void deleteEmployee() {
        when(employeeRepository.findByActive(1L)).thenReturn(Optional.of(employee));
        when(helpdeskTicketRepository.findByAssigneeIdAndDeletedFalse(1L)).thenReturn(Arrays.asList(new HelpdeskTicket()));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).findByActive(1L);
        verify(helpdeskTicketRepository, times(1)).findByAssigneeIdAndDeletedFalse(1L);
        assertTrue(employee.isDeleted());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void assignRoleToEmployee() {
        when(employeeRepository.findByActive(1L)).thenReturn(Optional.of(employee));
        when(roleRepository.findByActive(1L)).thenReturn(Optional.of(role));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toDto(any(Employee.class))).thenReturn(employeeDto);

        EmployeeDto updatedEmployee = employeeService.assignRoleToEmployee(1L, 1L);

        assertNotNull(updatedEmployee);
        assertTrue(updatedEmployee.getRoles().stream().anyMatch(r -> r.getName().equals(roleDto.getName())));
        verify(employeeRepository, times(1)).findByActive(1L);
        verify(roleRepository, times(1)).findByActive(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void getEmployeeById_NotFound() {
        when(employeeRepository.findByActive(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(1L));
        verify(employeeRepository, times(1)).findByActive(1L);
    }
}