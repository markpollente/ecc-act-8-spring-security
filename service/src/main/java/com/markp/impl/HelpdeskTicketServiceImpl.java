package com.markp.impl;

import com.markp.dto.HelpdeskTicketDto;
import com.markp.exception.ResourceNotFoundException;
import com.markp.logging.LogExecutionTime;
import com.markp.mapper.HelpdeskTicketMapper;
import com.markp.model.Employee;
import com.markp.model.HelpdeskTicket;
import com.markp.model.TicketStatus;
import com.markp.repository.EmployeeRepository;
import com.markp.repository.HelpdeskTicketRepository;
import com.markp.service.HelpdeskTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class HelpdeskTicketServiceImpl implements HelpdeskTicketService {

    @Autowired
    private HelpdeskTicketRepository ticketRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto createTicket(HelpdeskTicketDto ticketDto, String createdBy) {
        if (ticketDto.getTitle() == null || ticketDto.getTitle().isEmpty()) {
            throw new ResourceNotFoundException("Title is required");
        }
        if (ticketDto.getBody() == null || ticketDto.getBody().isEmpty()) {
            throw new ResourceNotFoundException("Body is required");
        }
        HelpdeskTicket ticket = HelpdeskTicketMapper.mapToHelpdeskTicket(ticketDto);
        ticket.setCreatedBy(createdBy);
        ticket.setStatus(TicketStatus.DRAFT);
        ticket.setTicketNo(generateTicketNo());
        HelpdeskTicket savedTicket = ticketRepository.save(ticket);
        return HelpdeskTicketMapper.mapToHelpdeskTicketDto(savedTicket);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public HelpdeskTicketDto getTicketById(Long ticketId) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket does not exist with given id: " + ticketId));

        return HelpdeskTicketMapper.mapToHelpdeskTicketDto(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public List<HelpdeskTicketDto> getAllTickets() {
        List<HelpdeskTicket> tickets = ticketRepository.findAll();
        return tickets.stream().map(HelpdeskTicketMapper::mapToHelpdeskTicketDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public List<HelpdeskTicketDto> getTicketsByStatus(String status) {
        List<HelpdeskTicket> tickets;
        try {
            tickets = ticketRepository.findByStatus(TicketStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid status: '" + status + "'. Valid statuses are: " +
                    Arrays.stream(TicketStatus.values()).map(Enum::name).collect(Collectors.joining(", ")), e);
        }
        return tickets.stream().map(HelpdeskTicketMapper::mapToHelpdeskTicketDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public List<HelpdeskTicketDto> getTicketsByAssignee(Long assigneeId) {
        List<HelpdeskTicket> tickets = ticketRepository.findByAssigneeId(assigneeId);
        return tickets.stream().map(HelpdeskTicketMapper::mapToHelpdeskTicketDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @LogExecutionTime
    public List<HelpdeskTicketDto> getTicketsByCreator(String createdBy) {
        List<HelpdeskTicket> tickets = ticketRepository.findByCreatedBy(createdBy);
        return tickets.stream().map(HelpdeskTicketMapper::mapToHelpdeskTicketDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto updateTicket(Long ticketId, HelpdeskTicketDto updatedTicket, String updatedBy) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
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
        return HelpdeskTicketMapper.mapToHelpdeskTicketDto(updatedTicketObj);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public void deleteTicket(Long ticketId) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket does not exist with given id: " + ticketId));

        ticketRepository.deleteById(ticketId);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto assignTicketToEmployee(Long ticketId, Long employeeId, String updatedBy) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket does not exist with given id: " + ticketId));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee does not exist with given id: " + employeeId));
        ticket.setAssignee(employee);
        ticket.setStatus(TicketStatus.FILED);
        ticket.setUpdatedBy(updatedBy);
        HelpdeskTicket updatedTicket = ticketRepository.save(ticket);
        return HelpdeskTicketMapper.mapToHelpdeskTicketDto(updatedTicket);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto addRemarkAndUpdateStatus(Long ticketId, String remarks, String status, String updatedBy) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
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
        return HelpdeskTicketMapper.mapToHelpdeskTicketDto(updatedTicket);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public HelpdeskTicketDto addRemarkAndUpdateStatusForEmployee(Long ticketId, String remarks, String status, String updatedBy) {
        HelpdeskTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket does not exist with given id: " + ticketId));
        Employee employee = employeeRepository.findByEmail(updatedBy);
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
        return HelpdeskTicketMapper.mapToHelpdeskTicketDto(updatedTicket);
    }

    private String generateTicketNo() {
        Random random = new Random();
        int number = random.nextInt(99999);
        return "Ticket #" + String.format("%05d", number);
    }
}