package com.markp.impl;

import com.markp.dto.RoleDto;
import com.markp.exception.ResourceNotFoundException;
import com.markp.logging.LogExecutionTime;
import com.markp.mapper.RoleMapper;
import com.markp.model.Employee;
import com.markp.model.Role;
import com.markp.repository.EmployeeRepository;
import com.markp.repository.RoleRepository;
import com.markp.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    @Transactional
    @LogExecutionTime
    public RoleDto createRole(RoleDto roleDto) {
        if (roleDto.getName() == null || roleDto.getName().isEmpty()) {
            throw new ResourceNotFoundException("Role name is required");
        }
        Role role = roleMapper.toEntity(roleDto);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public RoleDto getRoleByID(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role does not exist with given id: " + roleId));
        return roleMapper.toDto(role);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public List<RoleDto> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @LogExecutionTime
    public RoleDto updateRole(Long roleId, RoleDto updatedRole) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role does not exist with given id: " + roleId));
        role.setName(updatedRole.getName());
        role.setDescription(updatedRole.getDescription());
        Role updatedRoleObj = roleRepository.save(role);
        return roleMapper.toDto(updatedRoleObj);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role does not exist with given id: " + roleId));
        List<Employee> employees = employeeRepository.findAll();
        for (Employee employee : employees) {
            if (employee.getRoles() != null) {
                employee.getRoles().removeIf(r -> r.getId().equals(roleId));
                employeeRepository.save(employee);
            }
        }
        roleRepository.deleteById(roleId);
    }
}