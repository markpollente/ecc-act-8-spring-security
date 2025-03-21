package com.markp.repository;

import com.markp.model.HelpdeskTicket;
import com.markp.model.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HelpdeskTicketRepository extends BaseRepository<HelpdeskTicket, Long> {

    List<HelpdeskTicket> findByStatusAndDeletedFalse(TicketStatus status);

    List<HelpdeskTicket> findByAssigneeIdAndDeletedFalse(Long assigneeId);

    List<HelpdeskTicket> findByCreatedByAndDeletedFalse(String email);

    long countByStatusAndDeletedFalse(TicketStatus status);

    long countByCreatedByAndStatusAndDeletedFalse(String createdBy, TicketStatus status);

    long countByAssigneeIdAndStatusAndDeletedFalse(Long assigneeId, TicketStatus status);

    @Query("SELECT t FROM HelpdeskTicket t LEFT JOIN t.assignee e WHERE t.deleted = false " +
            "AND (:ticketNo IS NULL OR str(t.ticketNo) LIKE %:ticketNo%) " +
            "AND (:title IS NULL OR t.title LIKE %:title%) " +
            "AND (:body IS NULL OR t.body LIKE %:body%) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:assignee IS NULL OR (e.firstName LIKE %:assignee% OR e.lastName LIKE %:assignee%)) " +
            "AND (:createdBy IS NULL OR t.createdBy LIKE %:createdBy%) " +
            "AND (:updatedBy IS NULL OR t.updatedBy LIKE %:updatedBy%)" +
            "AND (cast(:createdDateStart as timestamp) IS NULL OR t.createdDate >= :createdDateStart) " +
            "AND (cast(:createdDateEnd as timestamp) IS NULL OR t.createdDate <= :createdDateEnd) " +
            "AND (cast(:updatedDateStart as timestamp) IS NULL OR t.updatedDate >= :updatedDateStart) " +
            "AND (cast(:updatedDateEnd as timestamp) IS NULL OR t.updatedDate <= :updatedDateEnd)")
    Page<HelpdeskTicket> findAllWithFilters(@Param("ticketNo") String ticketNo,
                                            @Param("title") String title,
                                            @Param("body") String body,
                                            @Param("status") TicketStatus status,
                                            @Param("assignee") String assignee,
                                            @Param("createdBy") String createdBy,
                                            @Param("updatedBy") String updatedBy,
                                            @Param("createdDateStart") LocalDateTime createdDateStart,
                                            @Param("createdDateEnd") LocalDateTime createdDateEnd,
                                            @Param("updatedDateStart") LocalDateTime updatedDateStart,
                                            @Param("updatedDateEnd") LocalDateTime updatedDateEnd,
                                            Pageable pageable);

    @Query("SELECT t FROM HelpdeskTicket t LEFT JOIN t.assignee e WHERE t.deleted = false " +
            "AND (:ticketNo IS NULL OR CAST(t.ticketNo as string) LIKE %:ticketNo%) " +
            "AND (:title IS NULL OR t.title LIKE %:title%) " +
            "AND (:body IS NULL OR t.body LIKE %:body%) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:assignee IS NULL OR (e.firstName LIKE %:assignee% OR e.lastName LIKE %:assignee%)) " +
            "AND (t.createdBy = :userEmail OR e.id = :employeeId) " + // Filter for relevant tickets
            "AND (:createdBy IS NULL OR t.createdBy LIKE %:createdBy%) " +
            "AND (:updatedBy IS NULL OR t.updatedBy LIKE %:updatedBy%)" +
            "AND (cast(:createdDateStart as timestamp) IS NULL OR t.createdDate >= :createdDateStart) " +
            "AND (cast(:createdDateEnd as timestamp) IS NULL OR t.createdDate <= :createdDateEnd) " +
            "AND (cast(:updatedDateStart as timestamp) IS NULL OR t.updatedDate >= :updatedDateStart) " +
            "AND (cast(:updatedDateEnd as timestamp) IS NULL OR t.updatedDate <= :updatedDateEnd)")
    Page<HelpdeskTicket> findRelevantTicketsWithFilters(@Param("ticketNo") String ticketNo,
                                                        @Param("title") String title,
                                                        @Param("body") String body,
                                                        @Param("status") TicketStatus status,
                                                        @Param("assignee") String assignee,
                                                        @Param("userEmail") String userEmail,
                                                        @Param("employeeId") Long employeeId,
                                                        @Param("createdBy") String createdBy,
                                                        @Param("updatedBy") String updatedBy,
                                                        @Param("createdDateStart") LocalDateTime createdDateStart,
                                                        @Param("createdDateEnd") LocalDateTime createdDateEnd,
                                                        @Param("updatedDateStart") LocalDateTime updatedDateStart,
                                                        @Param("updatedDateEnd") LocalDateTime updatedDateEnd,
                                                        Pageable pageable);
}