package com.markp.service;

import com.markp.dto.HelpdeskTicketDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface HelpdeskTicketService {
    HelpdeskTicketDto createTicket(HelpdeskTicketDto ticketDto);

    HelpdeskTicketDto getTicketById(Long ticketId);

    Page<HelpdeskTicketDto> getAllTickets(int page, int size,
                                          String ticketNo, String title,
                                          String body,String status, String assignee,
                                          LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
                                          LocalDateTime updatedDateStart, LocalDateTime updatedDateEnd);

    List<HelpdeskTicketDto> getTicketsByStatus(String status);

    List<HelpdeskTicketDto> getTicketsByAssignee(Long assigneeId);

    List<HelpdeskTicketDto> getTicketsByCreator(String createdBy);

    HelpdeskTicketDto updateTicket(Long ticketId, HelpdeskTicketDto updatedTicket);

    void deleteTicket(Long ticketId);

    HelpdeskTicketDto assignTicketToEmployee(Long ticketId, Long employeeId);

    HelpdeskTicketDto addRemarkAndUpdateStatus(Long ticketId, String remarks, String status);

    HelpdeskTicketDto addRemarkAndUpdateStatusForEmployee(Long ticketId, String remarks, String status, String updatedBy);
}