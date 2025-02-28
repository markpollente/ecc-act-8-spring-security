package com.markp.mapper;

import com.markp.dto.EmployeeDto;
import com.markp.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface EmployeeMapper {

    @Mapping(target = "roles", source = "roles")
    EmployeeDto toDto(Employee entity);

    @Mapping(target = "roles", source = "roles")
    Employee toEntity(EmployeeDto dto);

    void updateEntityFromDto(EmployeeDto dto, @MappingTarget Employee entity);
}