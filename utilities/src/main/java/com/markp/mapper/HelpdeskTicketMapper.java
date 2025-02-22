package com.markp.mapper;

import com.markp.dto.HelpdeskTicketDto;
import com.markp.model.HelpdeskTicket;

public class HelpdeskTicketMapper {

    public static HelpdeskTicketDto mapToHelpdeskTicketDto(HelpdeskTicket ticket) {
        return new HelpdeskTicketDto(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getTitle(),
                ticket.getBody(),
                EmployeeMapper.mapToEmployeeDto(ticket.getAssignee()),
                ticket.getStatus(),
                ticket.getCreatedDate(),
                ticket.getCreatedBy(),
                ticket.getUpdatedDate(),
                ticket.getUpdatedBy(),
                ticket.getRemarks()
        );
    }

    public static HelpdeskTicket mapToHelpdeskTicket(HelpdeskTicketDto ticketDto) {
        return new HelpdeskTicket(
                ticketDto.getId(),
                ticketDto.getTicketNo(),
                ticketDto.getTitle(),
                ticketDto.getBody(),
                ticketDto.getAssignee() != null ? EmployeeMapper.mapToEmployee(ticketDto.getAssignee()) : null,
                ticketDto.getStatus(),
                null,
                null,
                null,
                null,
                ticketDto.getRemarks()
        );
    }
}
