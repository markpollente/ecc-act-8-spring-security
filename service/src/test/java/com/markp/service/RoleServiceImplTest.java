package com.markp.service;

import com.markp.dto.RoleDto;
import com.markp.exception.ResourceNotFoundException;
import com.markp.service.impl.RoleServiceImpl;
import com.markp.mapper.RoleMapper;
import com.markp.model.Employee;
import com.markp.model.Role;
import com.markp.repository.EmployeeRepository;
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

import java.util.ArrayList;
import java.util.Arrays;
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

public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        role = new Role("ADMIN", "Administrator role", new ArrayList<>());
        role.setId(1L);
        roleDto = new RoleDto("ADMIN", "Administrator role");
    }

    @Test
    void createRole() {
        when(roleMapper.toEntity(any(RoleDto.class))).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(any(Role.class))).thenReturn(roleDto);

        RoleDto savedRole = roleService.createRole(roleDto);

        assertNotNull(savedRole);
        assertEquals(roleDto.getName(), savedRole.getName());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void getRoleById() {
        when(roleRepository.findByActive(1L)).thenReturn(Optional.of(role));
        when(roleMapper.toDto(any(Role.class))).thenReturn(roleDto);

        RoleDto foundRole = roleService.getRoleByID(1L);

        assertNotNull(foundRole);
        assertEquals(roleDto.getName(), foundRole.getName());
        verify(roleRepository, times(1)).findByActive(1L);
    }

    @Test
    void getAllRoles() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> rolePage = new PageImpl<>(Arrays.asList(role));
        when(roleRepository.findAllActive(pageable)).thenReturn(rolePage);

        Page<RoleDto> roles = roleService.getAllRoles(0, 10);

        assertNotNull(roles);
        assertEquals(1, roles.getTotalElements());
        verify(roleRepository, times(1)).findAllActive(pageable);
    }

    @Test
    void updateRole() {
        when(roleRepository.findByActive(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(any(Role.class))).thenReturn(roleDto);

        RoleDto updatedRole = roleService.updateRole(1L, roleDto);

        assertNotNull(updatedRole);
        assertEquals(roleDto.getName(), updatedRole.getName());
        verify(roleRepository, times(1)).findByActive(1L);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void deleteRole() {
        when(roleRepository.findByActive(1L)).thenReturn(Optional.of(role));
        Employee employee = new Employee();
        employee.setRoles(new ArrayList<>(Arrays.asList(role)));
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee));
        doNothing().when(roleRepository).deleteById(1L);

        roleService.deleteRole(1L);

        verify(roleRepository, times(1)).findByActive(1L);
        verify(employeeRepository, times(1)).findAll();
        assertTrue(role.isDeleted());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void getRoleById_NotFound() {
        when(roleRepository.findByActive(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleByID(1L));
        verify(roleRepository, times(1)).findByActive(1L);
    }
}