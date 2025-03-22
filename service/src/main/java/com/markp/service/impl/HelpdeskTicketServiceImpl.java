package com.markp.service.impl;

import com.markp.dto.HelpdeskTicketDto;
import com.markp.dto.request.HelpdeskTicketFilterRequest;
import com.markp.exception.ResourceNotFoundException;
import com.markp.logging.LogExecutionTime;
import com.markp.mapper.HelpdeskTicketMapper;
import com.markp.model.Employee;
import com.markp.model.HelpdeskTicket;
import com.markp.model.enums.TicketStatus;
import com.markp.repository.EmployeeRepository;
import com.markp.repository.HelpdeskTicketRepository;
import com.markp.service.HelpdeskTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@Service
public class HelpdeskTicketServiceImpl implements HelpdeskTicketService {

    private final HelpdeskTicketRepository ticketRepository;
    private final EmployeeRepository employeeRepository;
    private final HelpdeskTicketMapper helpdeskTicketMapper;

    @Autowired
    public HelpdeskTicketServiceImpl(HelpdeskTicketRepository ticketRepository,
                                     EmployeeRepository employeeRepository,
                                     HelpdeskTicketMapper helpdeskTicketMapper) {
        this.ticketRepository = ticketRepository;
        this.employeeRepository = employeeRepository;
        this.helpdeskTicketMapper = helpdeskTicketMapper;
    }

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto createTicket(HelpdeskTicketDto ticketDto) {
        HelpdeskTicket ticket = helpdeskTicketMapper.toEntity(ticketDto);
        ticket.setStatus(TicketStatus.DRAFT);
        HelpdeskTicket savedTicket = ticketRepository.save(ticket);
        return helpdeskTicketMapper.toDto(savedTicket);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public HelpdeskTicketDto getTicketById(Long ticketId) {
        HelpdeskTicket ticket = ticketRepository.findByActive(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket does not exist with given id: " + ticketId));
        return helpdeskTicketMapper.toDto(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public Page<HelpdeskTicketDto> getAllTickets(HelpdeskTicketFilterRequest filterRequest, Pageable pageable) {
        TicketStatus ticketStatus = null;
        if (filterRequest.getStatus() != null && !filterRequest.getStatus().isEmpty()) {
            try {
                ticketStatus = TicketStatus.valueOf(filterRequest.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: '" + filterRequest.getStatus() + "'. Valid statuses are: " +
                        Arrays.stream(TicketStatus.values()).map(Enum::name).collect(Collectors.joining(", ")), e);
            }
        }
        return ticketRepository
                .findAllWithFilters(filterRequest.getTicketNo(), filterRequest.getTitle(), filterRequest.getBody(), ticketStatus,
                        filterRequest.getAssignee(), filterRequest.getCreatedBy(), filterRequest.getUpdatedBy(),
                        filterRequest.getCreatedDateStart(), filterRequest.getCreatedDateEnd(),
                        filterRequest.getUpdatedDateStart(), filterRequest.getUpdatedDateEnd(), pageable)
                .map(helpdeskTicketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public List<HelpdeskTicketDto> getTicketsByStatus(String status) {
        List<HelpdeskTicket> tickets;
        try {
            tickets = ticketRepository.findByStatusAndDeletedFalse(TicketStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid status: '" + status + "'. Valid statuses are: " +
                    Arrays.stream(TicketStatus.values()).map(Enum::name).collect(Collectors.joining(", ")), e);
        }
        return tickets.stream().map(helpdeskTicketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public List<HelpdeskTicketDto> getTicketsByAssignee(Long assigneeId) {
        List<HelpdeskTicket> tickets = ticketRepository.findByAssigneeIdAndDeletedFalse(assigneeId);
        return tickets.stream().map(helpdeskTicketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public List<HelpdeskTicketDto> getTicketsByCreator(String createdBy) {
        Employee employee = employeeRepository.findByEmailAndDeletedFalse(createdBy);
        String employeeFullName = employee.getFirstName() + " " + employee.getLastName();
        List<HelpdeskTicket> tickets = ticketRepository.findByCreatedByAndDeletedFalse(employeeFullName);

        return tickets.stream().map(helpdeskTicketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto updateTicket(Long ticketId, HelpdeskTicketDto updatedTicket) {
        HelpdeskTicket ticket = ticketRepository.findByActive(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket does not exist with given id: " + ticketId));
        ticket.setTitle(updatedTicket.getTitle());
        ticket.setBody(updatedTicket.getBody());
        ticket.setStatus(updatedTicket.getStatus());
        if (updatedTicket.getRemarks() != null) {
            ticket.setRemarks(updatedTicket.getRemarks());
        }
        HelpdeskTicket updatedTicketObj = ticketRepository.save(ticket);
        return helpdeskTicketMapper.toDto(updatedTicketObj);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public void deleteTicket(Long ticketId) {
        HelpdeskTicket ticket = ticketRepository.findByActive(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket does not exist with given id: " + ticketId));
        ticket.setDeleted(true);
        ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto assignTicketToEmployee(Long ticketId, Long employeeId) {
        HelpdeskTicket ticket = ticketRepository.findByActive(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket does not exist with given id: " + ticketId));
        Employee employee = employeeRepository.findByActive(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee does not exist with given id: " + employeeId));
        ticket.setAssignee(employee);
        ticket.setStatus(TicketStatus.FILED);
        HelpdeskTicket updatedTicket = ticketRepository.save(ticket);
        return helpdeskTicketMapper.toDto(updatedTicket);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto addRemarkAndUpdateStatus(Long ticketId, String remarks, String status) {
        HelpdeskTicket ticket = ticketRepository.findByActive(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket does not exist with given id: " + ticketId));
        ticket.setRemarks(remarks);
        try {
            ticket.setStatus(TicketStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid status: '" + status + "'. Valid statuses are: " +
                    Arrays.stream(TicketStatus.values()).map(Enum::name).collect(Collectors.joining(", ")), e);
        }
        HelpdeskTicket updatedTicket = ticketRepository.save(ticket);
        return helpdeskTicketMapper.toDto(updatedTicket);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto addRemarkAndUpdateStatusForEmployee(Long ticketId, String remarks, String status, String updatedBy) {
        HelpdeskTicket ticket = ticketRepository.findByActive(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket does not exist with given id: " + ticketId));
        Employee employee = employeeRepository.findByEmailAndDeletedFalse(updatedBy);
        if (!ticket.getAssignee().getId().equals(employee.getId())) {
            throw new ResourceNotFoundException("You are not authorized to update this ticket.");
        }
        ticket.setRemarks(remarks);
        try {
            ticket.setStatus(TicketStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid status: '" + status + "'. Valid statuses are: " +
                    Arrays.stream(TicketStatus.values()).map(Enum::name).collect(Collectors.joining(", ")), e);
        }
        HelpdeskTicket updatedTicket = ticketRepository.save(ticket);
        return helpdeskTicketMapper.toDto(updatedTicket);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public Map<String, Long> getTicketCountsByStatus() {
        Map<String, Long> counts = new HashMap<>();
        for (TicketStatus status : TicketStatus.values()) {
            counts.put(status.name(), ticketRepository.countByStatusAndDeletedFalse(status));
        }
        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getPersonalTicketCounts(String email) {
        Map<String, Long> counts = new HashMap<>();

        Employee employee = employeeRepository.findByEmailAndDeletedFalse(email);
        if (employee == null) {
            return counts;
        }

        String employeeFullName = employee.getFirstName() + " " + employee.getLastName();

        for (TicketStatus status : TicketStatus.values()) {
            long createdCount = ticketRepository.countByCreatedByAndStatusAndDeletedFalse(employeeFullName, status);
            counts.put("CREATED_" + status.name(), createdCount);
        }

        for (TicketStatus status : TicketStatus.values()) {
            long assignedCount = ticketRepository.countByAssigneeIdAndStatusAndDeletedFalse(employee.getId(), status);
            counts.put("ASSIGNED_" + status.name(), assignedCount);
        }

        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public Page<HelpdeskTicketDto> getRelevantTickets(HelpdeskTicketFilterRequest filterRequest, String userEmail, Pageable pageable) {
        Employee employee = employeeRepository.findByEmailAndDeletedFalse(userEmail);
        if (employee == null) {
            return Page.empty(pageable);
        }

        TicketStatus ticketStatus = null;
        if (filterRequest.getStatus() != null && !filterRequest.getStatus().isEmpty()) {
            try {
                ticketStatus = TicketStatus.valueOf(filterRequest.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: '" + filterRequest.getStatus() + "'. Valid statuses are: " +
                        Arrays.stream(TicketStatus.values()).map(Enum::name).collect(Collectors.joining(", ")), e);
            }
        }

        return ticketRepository
                .findRelevantTicketsWithFilters(
                        filterRequest.getTicketNo(),
                        filterRequest.getTitle(),
                        filterRequest.getBody(),
                        ticketStatus,
                        filterRequest.getAssignee(),
                        userEmail,
                        employee.getId(),
                        filterRequest.getCreatedBy(),
                        filterRequest.getUpdatedBy(),
                        filterRequest.getCreatedDateStart(),
                        filterRequest.getCreatedDateEnd(),
                        filterRequest.getUpdatedDateStart(),
                        filterRequest.getUpdatedDateEnd(),
                        pageable)
                .map(helpdeskTicketMapper::toDto);
    }
}