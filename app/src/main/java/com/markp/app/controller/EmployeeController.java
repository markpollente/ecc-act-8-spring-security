package com.markp.app.controller;

import com.markp.dto.EmployeeDto;
import com.markp.dto.HelpdeskTicketDto;
import com.markp.dto.request.EmployeeFilterRequest;
import com.markp.dto.request.LoginRequest;
import com.markp.dto.response.LoginResponse;
import com.markp.security.JwtService;
import com.markp.service.EmployeeService;
import com.markp.service.HelpdeskTicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private HelpdeskTicketService helpdeskTicketService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    // testing purposes
    @PostMapping("/register")
    public ResponseEntity<EmployeeDto> registerEmployee(@RequestBody EmployeeDto employeeDto) {
        EmployeeDto savedEmployee = employeeService.createEmployee(employeeDto);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<LoginResponse> authenticateEmployee(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String jwt = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<EmployeeDto> getEmployeeProfile(Principal principal) {
        EmployeeDto employeeDto = employeeService.getEmployeeByEmail(principal.getName());
        return ResponseEntity.ok(employeeDto);
    }

    @GetMapping("/profile/filed")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<HelpdeskTicketDto>> getFiledTickets(Principal principal) {
        List<HelpdeskTicketDto> tickets = helpdeskTicketService.getTicketsByCreator(principal.getName());
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/profile/assigned")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<HelpdeskTicketDto>> getAssignedTickets(Principal principal) {
        EmployeeDto employee = employeeService.getEmployeeByEmail(principal.getName());
        List<HelpdeskTicketDto> tickets = helpdeskTicketService.getTicketsByAssignee(employee.getId());
        return ResponseEntity.ok(tickets);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeDto savedEmployee = employeeService.createEmployee(employeeDto);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") Long employeeId) {
        EmployeeDto employeeDto = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(employeeDto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<EmployeeDto>> getAllEmployees(EmployeeFilterRequest filterRequest, Pageable pageable) {
        Page<EmployeeDto> employees = employeeService.getAllEmployees(filterRequest, pageable);
        return ResponseEntity.ok(employees);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable("id") Long employeeId,
                                                      @RequestBody EmployeeDto updatedEmployee) {
        EmployeeDto employeeDto = employeeService.updateEmployee(employeeId, updatedEmployee);
        return ResponseEntity.ok(employeeDto);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok("Employee deleted successfully.");
    }

    @PutMapping("{employeeId}/assign-role/{roleId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<EmployeeDto> assignRoleToEmployee(@PathVariable("employeeId") Long employeeId,
                                                            @PathVariable("roleId") Long roleId) {
        EmployeeDto employeeDto = employeeService.assignRoleToEmployee(employeeId, roleId);
        return ResponseEntity.ok(employeeDto);
    }

    @PutMapping("/profile/assigned/{ticketId}/remark")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<HelpdeskTicketDto> addRemarkAndUpdateStatusForEmployee(@PathVariable("ticketId") Long ticketId,
                                                                                 @RequestParam("remarks") String remarks,
                                                                                 @RequestParam("status") String status,
                                                                                 Principal principal) {
        HelpdeskTicketDto ticketDto = helpdeskTicketService.addRemarkAndUpdateStatusForEmployee(ticketId, remarks, status, principal.getName());
        return ResponseEntity.ok(ticketDto);
    }
}