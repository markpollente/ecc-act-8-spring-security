package com.markp.service;

import com.markp.dto.RoleDto;
import com.markp.exception.ResourceNotFoundException;
import com.markp.impl.RoleServiceImpl;
import com.markp.model.Employee;
import com.markp.model.Role;
import com.markp.repository.EmployeeRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        role = new Role(1L, "Admin", "Administrator role", null);
        roleDto = new RoleDto(1L, "Admin", "Administrator role");
    }

    @Test
    void createRole() {
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDto savedRole = roleService.createRole(roleDto);

        assertNotNull(savedRole);
        assertEquals(roleDto.getName(), savedRole.getName());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void getRoleById() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        RoleDto foundRole = roleService.getRoleByID(1L);

        assertNotNull(foundRole);
        assertEquals(roleDto.getName(), foundRole.getName());
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    void getAllRoles() {
        when(roleRepository.findAll()).thenReturn(Arrays.asList(role));

        List<RoleDto> roles = roleService.getAllRoles();

        assertNotNull(roles);
        assertEquals(1, roles.size());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void updateRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDto updatedRole = roleService.updateRole(1L, roleDto);

        assertNotNull(updatedRole);
        assertEquals(roleDto.getName(), updatedRole.getName());
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void deleteRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        Employee employee = new Employee();
        employee.setRoles(new ArrayList<>(Arrays.asList(role)));
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee));
        doNothing().when(roleRepository).deleteById(1L);

        roleService.deleteRole(1L);

        verify(roleRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findAll();
        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    void getRoleById_NotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleByID(1L));
        verify(roleRepository, times(1)).findById(1L);
    }
}