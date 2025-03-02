package com.markp.repository;

import com.markp.model.HelpdeskTicket;
import com.markp.model.enums.TicketStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface HelpdeskTicketRepository extends BaseRepository<HelpdeskTicket, Long> {

    List<HelpdeskTicket> findByStatusAndDeletedFalse(TicketStatus status);

    List<HelpdeskTicket> findByAssigneeIdAndDeletedFalse(Long assigneeId);

    List<HelpdeskTicket> findByCreatedByAndDeletedFalse(String email);

    List<HelpdeskTicket> findByStatusAndCreatedDateBetweenAndDeletedFalse(TicketStatus status, LocalDateTime startDate, LocalDateTime endDate);
}