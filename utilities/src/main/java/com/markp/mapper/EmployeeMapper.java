package com.markp.mapper;

import com.markp.dto.EmployeeDto;
import com.markp.model.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface EmployeeMapper {

    EmployeeDto toDto(Employee entity);

    Employee toEntity(EmployeeDto dto);
}