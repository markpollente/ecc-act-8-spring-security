package com.markp.service;

import com.markp.dto.RoleDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {

    RoleDto createRole(RoleDto roleDto, String createdBy);

    RoleDto getRoleByID(Long roleId);

    Page<RoleDto> getAllRoles(int page, int size);

    RoleDto updateRole(Long roleId, RoleDto updatedRole, String updatedBy);

    void deleteRole(Long roleId);

}
