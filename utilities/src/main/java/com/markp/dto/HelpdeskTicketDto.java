package com.markp.dto;

import com.markp.model.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HelpdeskTicketDto extends BaseDto {
    private Long id;
    private String ticketNo;
    private String title;
    private String body;
    private EmployeeDto assignee;
    private TicketStatus status;
    private String remarks;
}
