package com.markp.service;

import com.markp.dto.HelpdeskTicketDto;
import com.markp.exception.ResourceNotFoundException;
import com.markp.impl.HelpdeskTicketServiceImpl;
import com.markp.model.Employee;
import com.markp.model.HelpdeskTicket;
import com.markp.model.TicketStatus;
import com.markp.repository.EmployeeRepository;
import com.markp.repository.HelpdeskTicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @InjectMocks
    private HelpdeskTicketServiceImpl ticketService;

    private HelpdeskTicket ticket;
    private HelpdeskTicketDto ticketDto;
    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee(1L, "Mark", "Pollente", "markp@hmm.com", 30, "123 Antipolo St", "0951234678", "Active", null);
        ticket = new HelpdeskTicket(1L, "Ticket #000001", "Issue", "Description", employee, TicketStatus.FILED, null, "system", null, "system", "remark");
        ticketDto = new HelpdeskTicketDto(1L, "Ticket #000001", "Issue", "Description", null, TicketStatus.FILED, null, "system", null, "system", "remark");
    }

    @Test
    void createTicket() {
        when(ticketRepository.save(any(HelpdeskTicket.class))).thenReturn(ticket);

        HelpdeskTicketDto savedTicket = ticketService.createTicket(ticketDto);

        assertNotNull(savedTicket);
        assertEquals(ticketDto.getTitle(), savedTicket.getTitle());
        verify(ticketRepository, times(1)).save(any(HelpdeskTicket.class));
    }

    @Test
    void getTicketById() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        HelpdeskTicketDto foundTicket = ticketService.getTicketById(1L);

        assertNotNull(foundTicket);
        assertEquals(ticketDto.getTitle(), foundTicket.getTitle());
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void getAllTickets() {
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticket));

        List<HelpdeskTicketDto> tickets = ticketService.getAllTickets();

        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void getTicketsByStatus() {
        when(ticketRepository.findByStatus(TicketStatus.FILED)).thenReturn(Arrays.asList(ticket));

        List<HelpdeskTicketDto> tickets = ticketService.getTicketsByStatus("filed");

        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        verify(ticketRepository, times(1)).findByStatus(TicketStatus.FILED);
    }

    @Test
    void getTicketsByAssignee() {
        when(ticketRepository.findByAssigneeId(1L)).thenReturn(Arrays.asList(ticket));

        List<HelpdeskTicketDto> tickets = ticketService.getTicketsByAssignee(1L);

        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        verify(ticketRepository, times(1)).findByAssigneeId(1L);
    }

    @Test
    void updateTicket() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(HelpdeskTicket.class))).thenReturn(ticket);

        HelpdeskTicketDto updatedTicket = ticketService.updateTicket(1L, ticketDto);

        assertNotNull(updatedTicket);
        assertEquals(ticketDto.getTitle(), updatedTicket.getTitle());
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(any(HelpdeskTicket.class));
    }

    @Test
    void deleteTicket() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        doNothing().when(ticketRepository).deleteById(1L);

        ticketService.deleteTicket(1L);

        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).deleteById(1L);
    }

    @Test
    void assignTicketToEmployee() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(ticketRepository.save(any(HelpdeskTicket.class))).thenReturn(ticket);

        HelpdeskTicketDto updatedTicket = ticketService.assignTicketToEmployee(1L, 1L);

        assertNotNull(updatedTicket);
        assertEquals(employee.getFirstName(), updatedTicket.getAssignee().getFirstName());
        verify(ticketRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(any(HelpdeskTicket.class));
    }

    @Test
    void addRemarkAndUpdateStatus() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(HelpdeskTicket.class))).thenReturn(ticket);

        HelpdeskTicketDto updatedTicket = ticketService.addRemarkAndUpdateStatus(1L, "new remark", "inprogress");

        assertNotNull(updatedTicket);
        assertEquals("new remark", updatedTicket.getRemarks());
        assertEquals(TicketStatus.INPROGRESS, updatedTicket.getStatus());
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(any(HelpdeskTicket.class));
    }

    @Test
    void getTicketById_NotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ticketService.getTicketById(1L));
        verify(ticketRepository, times(1)).findById(1L);
    }
}