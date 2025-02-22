package com.markp.mapper;

import com.markp.dto.RoleDto;
import com.markp.model.Role;

public class RoleMapper {

    public static RoleDto mapToRoleDto(Role role) {
        return new RoleDto(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }

    public static Role mapToRole(RoleDto roleDto) {
        return new Role(
                roleDto.getId(),
                roleDto.getName(),
                roleDto.getDescription(),
                null
        );
    }
}