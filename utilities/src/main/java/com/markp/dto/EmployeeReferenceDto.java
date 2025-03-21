package com.markp.dto;

import lombok.Data;

@Data
public class EmployeeReferenceDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
}