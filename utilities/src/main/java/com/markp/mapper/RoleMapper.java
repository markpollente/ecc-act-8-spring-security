package com.markp.mapper;

import com.markp.dto.RoleDto;
import com.markp.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toDto(Role entity);

    Role toEntity(RoleDto dto);
}