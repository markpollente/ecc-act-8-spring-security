package com.markp.service;

import com.markp.dto.HelpdeskTicketDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HelpdeskTicketService {
    HelpdeskTicketDto createTicket(HelpdeskTicketDto ticketDto, String createdBy);

    HelpdeskTicketDto getTicketById(Long ticketId);

    Page<HelpdeskTicketDto> getAllTickets(int page, int size);

    List<HelpdeskTicketDto> getTicketsByStatus(String status);

    List<HelpdeskTicketDto> getTicketsByAssignee(Long assigneeId);

    List<HelpdeskTicketDto> getTicketsByCreator(String createdBy);

    HelpdeskTicketDto updateTicket(Long ticketId, HelpdeskTicketDto updatedTicket, String updatedBy);

    void deleteTicket(Long ticketId);

    HelpdeskTicketDto assignTicketToEmployee(Long ticketId, Long employeeId, String updatedBy);

    HelpdeskTicketDto addRemarkAndUpdateStatus(Long ticketId, String remarks, String status, String updatedBy);

    HelpdeskTicketDto addRemarkAndUpdateStatusForEmployee(Long ticketId, String remarks, String status, String updatedBy);
}