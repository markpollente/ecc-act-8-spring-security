package com.markp.impl;

import com.markp.dto.HelpdeskTicketDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@Service
public class HelpdeskTicketServiceImpl implements HelpdeskTicketService {

    @Autowired
    private HelpdeskTicketRepository ticketRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    HelpdeskTicketMapper helpdeskTicketMapper;

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto createTicket(HelpdeskTicketDto ticketDto, String createdBy) {
        HelpdeskTicket ticket = helpdeskTicketMapper.toEntity(ticketDto);
        ticket.setCreatedBy(createdBy);
        ticket.setUpdatedBy(createdBy);
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
    public Page<HelpdeskTicketDto> getAllTickets(int page, int size, String ticketNo, String title, String body, String status, String assignee, LocalDateTime createdDateStart, LocalDateTime createdDateEnd, LocalDateTime updatedDateStart, LocalDateTime updatedDateEnd) {
        PageRequest pageRequest = PageRequest.of(page, size);
        TicketStatus ticketStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                ticketStatus = TicketStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: '" + status + "'. Valid statuses are: " +
                        Arrays.stream(TicketStatus.values()).map(Enum::name).collect(Collectors.joining(", ")), e);
            }
        }
        return ticketRepository
                .findAllWithFilters(ticketNo, title, body, ticketStatus, assignee, createdDateStart, createdDateEnd, updatedDateStart, updatedDateEnd, pageRequest)
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
        List<HelpdeskTicket> tickets = ticketRepository.findByCreatedByAndDeletedFalse(createdBy);
        return tickets.stream().map(helpdeskTicketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto updateTicket(Long ticketId, HelpdeskTicketDto updatedTicket, String updatedBy) {
        HelpdeskTicket ticket = ticketRepository.findByActive(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket does not exist with given id: " + ticketId));
        ticket.setTitle(updatedTicket.getTitle());
        ticket.setBody(updatedTicket.getBody());
        ticket.setStatus(updatedTicket.getStatus());
        ticket.setUpdatedBy(updatedBy);
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
    public HelpdeskTicketDto assignTicketToEmployee(Long ticketId, Long employeeId, String updatedBy) {
        HelpdeskTicket ticket = ticketRepository.findByActive(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket does not exist with given id: " + ticketId));
        Employee employee = employeeRepository.findByActive(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee does not exist with given id: " + employeeId));
        ticket.setAssignee(employee);
        ticket.setStatus(TicketStatus.FILED);
        ticket.setUpdatedBy(updatedBy);
        HelpdeskTicket updatedTicket = ticketRepository.save(ticket);
        return helpdeskTicketMapper.toDto(updatedTicket);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto addRemarkAndUpdateStatus(Long ticketId, String remarks, String status, String updatedBy) {
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
        ticket.setUpdatedBy(updatedBy);
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
        if (employee == null) {
            throw new ResourceNotFoundException("Employee does not exist with given email: " + updatedBy);
        }
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
        ticket.setUpdatedBy(updatedBy);
        HelpdeskTicket updatedTicket = ticketRepository.save(ticket);
        return helpdeskTicketMapper.toDto(updatedTicket);
    }
}