package com.markp.service;

import com.markp.dto.EmployeeDto;
import com.markp.dto.HelpdeskTicketDto;
import com.markp.dto.request.HelpdeskTicketFilterRequest;
import com.markp.exception.ResourceNotFoundException;
import com.markp.impl.HelpdeskTicketServiceImpl;
import com.markp.mapper.HelpdeskTicketMapper;
import com.markp.model.Employee;
import com.markp.model.HelpdeskTicket;
import com.markp.model.enums.TicketStatus;
import com.markp.repository.EmployeeRepository;
import com.markp.repository.HelpdeskTicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HelpdeskTicketServiceImplTest {

    @Mock
    private HelpdeskTicketRepository ticketRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private HelpdeskTicketMapper helpdeskTicketMapper;

    @InjectMocks
    private HelpdeskTicketServiceImpl ticketService;

    private HelpdeskTicket ticket;
    private HelpdeskTicketDto ticketDto;
    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee("Mark", "Pollente", "markp@hmm.com", LocalDate.of(1990, 1, 1), 30, "123 Antipolo St", "0951234678", "Active", "password", null);
        ticket = new HelpdeskTicket(UUID.randomUUID(), "Issue", "Description", employee, TicketStatus.FILED, "remark");
        ticketDto = new HelpdeskTicketDto(UUID.randomUUID(), "Issue", "Description", null, TicketStatus.FILED, "remark");
    }

    @Test
    void createTicket() {
        when(helpdeskTicketMapper.toEntity(any(HelpdeskTicketDto.class))).thenReturn(ticket);
        when(ticketRepository.save(any(HelpdeskTicket.class))).thenReturn(ticket);
        when(helpdeskTicketMapper.toDto(any(HelpdeskTicket.class))).thenReturn(ticketDto);

        HelpdeskTicketDto savedTicket = ticketService.createTicket(ticketDto);

        assertNotNull(savedTicket);
        assertEquals(ticketDto.getTitle(), savedTicket.getTitle());
        verify(ticketRepository, times(1)).save(any(HelpdeskTicket.class));
    }

    @Test
    void getTicketById() {
        when(ticketRepository.findByActive(1L)).thenReturn(Optional.of(ticket));
        when(helpdeskTicketMapper.toDto(any(HelpdeskTicket.class))).thenReturn(ticketDto);

        HelpdeskTicketDto foundTicket = ticketService.getTicketById(1L);

        assertNotNull(foundTicket);
        assertEquals(ticketDto.getTitle(), foundTicket.getTitle());
        verify(ticketRepository, times(1)).findByActive(1L);
    }

    @Test
    void getAllTickets() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<HelpdeskTicket> ticketPage = new PageImpl<>(Arrays.asList(ticket));
        when(ticketRepository.findAllWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(ticketPage);

        Page<HelpdeskTicketDto> tickets = ticketService.getAllTickets(new HelpdeskTicketFilterRequest(), pageable);

        assertNotNull(tickets);
        assertEquals(1, tickets.getTotalElements());
        verify(ticketRepository, times(1)).findAllWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void getTicketsByStatus() {
        when(ticketRepository.findByStatusAndDeletedFalse(TicketStatus.FILED)).thenReturn(Arrays.asList(ticket));
        when(helpdeskTicketMapper.toDto(any(HelpdeskTicket.class))).thenReturn(ticketDto);

        List<HelpdeskTicketDto> tickets = ticketService.getTicketsByStatus("filed");

        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        verify(ticketRepository, times(1)).findByStatusAndDeletedFalse(TicketStatus.FILED);
    }

    @Test
    void getTicketsByAssignee() {
        when(ticketRepository.findByAssigneeIdAndDeletedFalse(1L)).thenReturn(Arrays.asList(ticket));
        when(helpdeskTicketMapper.toDto(any(HelpdeskTicket.class))).thenReturn(ticketDto);

        List<HelpdeskTicketDto> tickets = ticketService.getTicketsByAssignee(1L);

        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        verify(ticketRepository, times(1)).findByAssigneeIdAndDeletedFalse(1L);
    }

    @Test
    void updateTicket() {
        when(ticketRepository.findByActive(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(HelpdeskTicket.class))).thenReturn(ticket);
        when(helpdeskTicketMapper.toDto(any(HelpdeskTicket.class))).thenReturn(ticketDto);

        HelpdeskTicketDto updatedTicket = ticketService.updateTicket(1L, ticketDto);

        assertNotNull(updatedTicket);
        assertEquals(ticketDto.getTitle(), updatedTicket.getTitle());
        verify(ticketRepository, times(1)).findByActive(1L);
        verify(ticketRepository, times(1)).save(any(HelpdeskTicket.class));
    }

    @Test
    void deleteTicket() {
        when(ticketRepository.findByActive(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(HelpdeskTicket.class))).thenReturn(ticket);

        ticketService.deleteTicket(1L);

        verify(ticketRepository, times(1)).findByActive(1L);
        assertTrue(ticket.isDeleted());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void assignTicketToEmployee() {
        when(ticketRepository.findByActive(1L)).thenReturn(Optional.of(ticket));
        when(employeeRepository.findByActive(1L)).thenReturn(Optional.of(employee));
        when(ticketRepository.save(any(HelpdeskTicket.class))).thenReturn(ticket);
        when(helpdeskTicketMapper.toDto(any(HelpdeskTicket.class))).thenReturn(ticketDto);

        ticketDto.setAssignee(new EmployeeDto("Mark", "Pollente", "markp@hmm.com", LocalDate.of(1990, 1, 1), 30, "123 Antipolo St", "0951234678", "Active", "password", null));
        HelpdeskTicketDto updatedTicket = ticketService.assignTicketToEmployee(1L, 1L);

        assertNotNull(updatedTicket);
        assertEquals(employee.getFirstName(), updatedTicket.getAssignee().getFirstName());
        verify(ticketRepository, times(1)).findByActive(1L);
        verify(employeeRepository, times(1)).findByActive(1L);
        verify(ticketRepository, times(1)).save(any(HelpdeskTicket.class));
    }

    @Test
    void addRemarkAndUpdateStatus() {
        when(ticketRepository.findByActive(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(HelpdeskTicket.class))).thenReturn(ticket);
        when(helpdeskTicketMapper.toDto(any(HelpdeskTicket.class))).thenAnswer(invocation -> {
            HelpdeskTicket savedTicket = invocation.getArgument(0);
            return new HelpdeskTicketDto(savedTicket.getTicketNo(), savedTicket.getTitle(), savedTicket.getBody(), null, savedTicket.getStatus(), savedTicket.getRemarks());
        });

        HelpdeskTicketDto updatedTicket = ticketService.addRemarkAndUpdateStatus(1L, "new remark", "inprogress");

        assertNotNull(updatedTicket);
        assertEquals("new remark", updatedTicket.getRemarks());
        assertEquals(TicketStatus.INPROGRESS, updatedTicket.getStatus());
        verify(ticketRepository, times(1)).findByActive(1L);
        verify(ticketRepository, times(1)).save(any(HelpdeskTicket.class));
    }

    @Test
    void getTicketById_NotFound() {
        when(ticketRepository.findByActive(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ticketService.getTicketById(1L));
        verify(ticketRepository, times(1)).findByActive(1L);
    }
}