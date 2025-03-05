package com.markp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeFilterRequest extends BaseFilterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String employmentStatus;
}