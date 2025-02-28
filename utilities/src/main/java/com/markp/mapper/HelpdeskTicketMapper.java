package com.markp.mapper;

import com.markp.dto.HelpdeskTicketDto;
import com.markp.model.HelpdeskTicket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface HelpdeskTicketMapper {

    @Mapping(target = "assignee", source = "assignee")
    HelpdeskTicketDto toDto(HelpdeskTicket entity);

    @Mapping(target = "assignee", source = "assignee")
    HelpdeskTicket toEntity(HelpdeskTicketDto dto);

    void updateEntityFromDto(HelpdeskTicketDto dto, @MappingTarget HelpdeskTicket entity);
}