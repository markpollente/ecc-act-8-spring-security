package com.markp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HelpdeskTicketFilterRequest extends BaseFilterRequest {
    private String ticketNo;
    private String title;
    private String body;
    private String status;
    private String assignee;
}
