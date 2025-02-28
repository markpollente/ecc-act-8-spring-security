package com.markp.dto;

import com.markp.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto extends BaseDto {
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    private String address;
    private String contactNumber;
    private String employmentStatus;
    private String password;
    private List<RoleDto> roles;
}
