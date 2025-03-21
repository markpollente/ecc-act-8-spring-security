package com.markp.service;

import com.markp.dto.HelpdeskTicketDto;
import com.markp.dto.request.HelpdeskTicketFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface HelpdeskTicketService {
    HelpdeskTicketDto createTicket(HelpdeskTicketDto ticketDto);

    HelpdeskTicketDto getTicketById(Long ticketId);

    Page<HelpdeskTicketDto> getAllTickets(HelpdeskTicketFilterRequest filterRequest, Pageable pageable);

    List<HelpdeskTicketDto> getTicketsByStatus(String status);

    List<HelpdeskTicketDto> getTicketsByAssignee(Long assigneeId);

    List<HelpdeskTicketDto> getTicketsByCreator(String createdBy);

    HelpdeskTicketDto updateTicket(Long ticketId, HelpdeskTicketDto updatedTicket);

    void deleteTicket(Long ticketId);

    HelpdeskTicketDto assignTicketToEmployee(Long ticketId, Long employeeId);

    HelpdeskTicketDto addRemarkAndUpdateStatus(Long ticketId, String remarks, String status);

    HelpdeskTicketDto addRemarkAndUpdateStatusForEmployee(Long ticketId, String remarks, String status, String updatedBy);

    Map<String, Long> getTicketCountsByStatus();

    Map<String, Long> getPersonalTicketCounts(String email);

    Page<HelpdeskTicketDto> getRelevantTickets(HelpdeskTicketFilterRequest filterRequest, String userEmail, Pageable pageable);
}