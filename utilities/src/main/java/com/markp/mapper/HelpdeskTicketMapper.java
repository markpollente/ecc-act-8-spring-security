package com.markp.mapper;

import com.markp.dto.HelpdeskTicketDto;
import com.markp.model.HelpdeskTicket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface HelpdeskTicketMapper {

    HelpdeskTicketDto toDto(HelpdeskTicket entity);

    HelpdeskTicket toEntity(HelpdeskTicketDto dto);
}