package com.markp.app.controller;

import com.markp.dto.HelpdeskTicketDto;
import com.markp.service.HelpdeskTicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class HelpdeskTicketController {

    @Autowired
    private HelpdeskTicketService ticketService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<HelpdeskTicketDto> createTicket(@Valid @RequestBody HelpdeskTicketDto ticketDto, Principal principal) {
        HelpdeskTicketDto savedTicket = ticketService.createTicket(ticketDto, principal.getName());
        return new ResponseEntity<>(savedTicket, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<HelpdeskTicketDto> getTicketById(@PathVariable("id") Long ticketId) {
        HelpdeskTicketDto ticketDto = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(ticketDto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<HelpdeskTicketDto>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String ticketNo,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String body,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdDateEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedDateEnd) {
        Page<HelpdeskTicketDto> tickets = ticketService.getAllTickets(page, size, ticketNo, title, body, status, assignee, createdDateStart, createdDateEnd, updatedDateStart, updatedDateEnd);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<HelpdeskTicketDto>> getTicketsByStatus(@PathVariable("status") String status) {
        List<HelpdeskTicketDto> tickets = ticketService.getTicketsByStatus(status);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/assignee/{assigneeId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<HelpdeskTicketDto>> getTicketsByAssignee(@PathVariable("assigneeId") Long assigneeId) {
        List<HelpdeskTicketDto> tickets = ticketService.getTicketsByAssignee(assigneeId);
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<HelpdeskTicketDto> updateTicket(@PathVariable("id") Long ticketId,
                                                          @RequestBody HelpdeskTicketDto updatedTicket,
                                                          Principal principal) {
        HelpdeskTicketDto ticketDto = ticketService.updateTicket(ticketId, updatedTicket, principal.getName());
        return ResponseEntity.ok(ticketDto);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> deleteTicket(@PathVariable("id") Long ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.ok("Ticket deleted successfully.");
    }

    @PutMapping("{ticketId}/assign/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<HelpdeskTicketDto> assignTicketToEmployee(@PathVariable("ticketId") Long ticketId,
                                                                    @PathVariable("employeeId") Long employeeId,
                                                                    Principal principal) {
        HelpdeskTicketDto ticketDto = ticketService.assignTicketToEmployee(ticketId, employeeId, principal.getName());
        return ResponseEntity.ok(ticketDto);
    }

    @PutMapping("{ticketId}/remark")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<HelpdeskTicketDto> addRemarkAndUpdateStatus(@PathVariable("ticketId") Long ticketId,
                                                                      @RequestParam("remarks") String remarks,
                                                                      @RequestParam("status") String status,
                                                                      Principal principal) {
        HelpdeskTicketDto ticketDto = ticketService.addRemarkAndUpdateStatus(ticketId, remarks, status, principal.getName());
        return ResponseEntity.ok(ticketDto);
    }
}