package com.markp.mapper;

import com.markp.dto.EmployeeDto;
import com.markp.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface EmployeeMapper {

    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "password", ignore = true)
    EmployeeDto toDto(Employee entity);

    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "password", ignore = true)
    Employee toEntity(EmployeeDto dto);
}