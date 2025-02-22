package com.markp.dto;

import com.markp.model.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HelpdeskTicketDto {
    private Long id;
    private String ticketNo;
    private String title;
    private String body;
    private EmployeeDto assignee;
    private TicketStatus status;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private String remarks;
}
