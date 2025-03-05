package com.markp.repository;

import com.markp.model.HelpdeskTicket;
import com.markp.model.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface HelpdeskTicketRepository extends BaseRepository<HelpdeskTicket, Long> {

    List<HelpdeskTicket> findByStatusAndDeletedFalse(TicketStatus status);

    List<HelpdeskTicket> findByAssigneeIdAndDeletedFalse(Long assigneeId);

    List<HelpdeskTicket> findByCreatedByAndDeletedFalse(String email);

    @Query("SELECT t FROM HelpdeskTicket t LEFT JOIN t.assignee e WHERE t.deleted = false " +
            "AND (:ticketNo IS NULL OR str(t.ticketNo) LIKE %:ticketNo%) " +
            "AND (:title IS NULL OR t.title LIKE %:title%) " +
            "AND (:body IS NULL OR t.body LIKE %:body%) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:assignee IS NULL OR e.email LIKE %:assignee%) " +
            "AND (cast(:createdDateStart as timestamp) IS NULL OR t.createdDate >= :createdDateStart) " +
            "AND (cast(:createdDateEnd as timestamp) IS NULL OR t.createdDate <= :createdDateEnd) " +
            "AND (cast(:updatedDateStart as timestamp) IS NULL OR t.updatedDate >= :updatedDateStart) " +
            "AND (cast(:updatedDateEnd as timestamp) IS NULL OR t.updatedDate <= :updatedDateEnd)")
    Page<HelpdeskTicket> findAllWithFilters(@Param("ticketNo") String ticketNo,
                                            @Param("title") String title,
                                            @Param("body") String body,
                                            @Param("status") TicketStatus status,
                                            @Param("assignee") String assignee,
                                            @Param("createdDateStart") LocalDateTime createdDateStart,
                                            @Param("createdDateEnd") LocalDateTime createdDateEnd,
                                            @Param("updatedDateStart") LocalDateTime updatedDateStart,
                                            @Param("updatedDateEnd") LocalDateTime updatedDateEnd,
                                            Pageable pageable);
}