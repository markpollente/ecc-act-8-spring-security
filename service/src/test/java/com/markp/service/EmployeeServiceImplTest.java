package com.markp.service;

import com.markp.dto.EmployeeDto;
import com.markp.dto.RoleDto;
import com.markp.exception.ResourceNotFoundException;
import com.markp.impl.EmployeeServiceImpl;
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

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeDto employeeDto;
    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        role = new Role(1L, "Admin", "Administrator role", null);
        roleDto = new RoleDto(1L, "Admin", "Administrator role");
        employee = new Employee(1L, "First", "Last", "first.last@gmail.com", 30, "123 Antipolo St", "0951234678", "Active", new ArrayList<>(Arrays.asList(role)));
        employeeDto = new EmployeeDto(1L, "First", "Last", "first.last@gmail.com", 30, "123 Antipolo St", "0951234678", "Active", new ArrayList<>(Arrays.asList(roleDto)));
    }

    @Test
    void createEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto savedEmployee = employeeService.createEmployee(employeeDto);

        assertNotNull(savedEmployee);
        assertEquals(employeeDto.getFirstName(), savedEmployee.getFirstName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void getEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeDto foundEmployee = employeeService.getEmployeeById(1L);

        assertNotNull(foundEmployee);
        assertEquals(employeeDto.getFirstName(), foundEmployee.getFirstName());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee));

        List<EmployeeDto> employees = employeeService.getAllEmployees();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void updateEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto updatedEmployee = employeeService.updateEmployee(1L, employeeDto);

        assertNotNull(updatedEmployee);
        assertEquals(employeeDto.getFirstName(), updatedEmployee.getFirstName());
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void deleteEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(helpdeskTicketRepository.findByAssigneeId(1L)).thenReturn(Arrays.asList(new HelpdeskTicket()));
        doNothing().when(employeeRepository).deleteById(1L);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).findById(1L);
        verify(helpdeskTicketRepository, times(1)).findByAssigneeId(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void assignRoleToEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto updatedEmployee = employeeService.assignRoleToEmployee(1L, 1L);

        assertNotNull(updatedEmployee);
        assertTrue(updatedEmployee.getRoles().stream().anyMatch(r -> r.getName().equals(roleDto.getName())));
        verify(employeeRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void getEmployeeById_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(1L));
        verify(employeeRepository, times(1)).findById(1L);
    }
}