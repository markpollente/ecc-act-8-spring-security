package com.markp.service;

import com.markp.dto.RoleDto;
import org.springframework.data.domain.Page;

public interface RoleService {

    RoleDto createRole(RoleDto roleDto);

    RoleDto getRoleByID(Long roleId);

    Page<RoleDto> getAllRoles(int page, int size);

    RoleDto updateRole(Long roleId, RoleDto updatedRole);

    void deleteRole(Long roleId);

}
