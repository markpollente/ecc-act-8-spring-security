package com.markp.app.controller;

import com.markp.dto.EmployeeDto;
import com.markp.dto.EmployeeReferenceDto;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final HelpdeskTicketService helpdeskTicketService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Autowired
    public EmployeeController(EmployeeService employeeService,
                              HelpdeskTicketService helpdeskTicketService,
                              AuthenticationManager authenticationManager,
                              UserDetailsService userDetailsService,
                              JwtService jwtService) {
        this.employeeService = employeeService;
        this.helpdeskTicketService = helpdeskTicketService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
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

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> updateEmployeeProfile(@RequestBody EmployeeDto updatedProfile, Principal principal) {
        String currentEmail = principal.getName();
        EmployeeDto updatedEmployee = employeeService.updateEmployeeProfile(principal.getName(), updatedProfile);

        Map<String, Object> response = new HashMap<>();
        response.put("employee", updatedEmployee);

        boolean emailChanged = !currentEmail.equals(updatedEmployee.getEmail());
        response.put("emailChanged", emailChanged);

        if (emailChanged) {
            response.put("message", "Email address updated. Please log in with your new email address.");
        }
        return ResponseEntity.ok(response);
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeDto savedEmployee = employeeService.createEmployee(employeeDto);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") Long employeeId) {
        EmployeeDto employeeDto = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(employeeDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EmployeeDto>> getAllEmployees(EmployeeFilterRequest filterRequest, Pageable pageable) {
        Page<EmployeeDto> employees = employeeService.getAllEmployees(filterRequest, pageable);
        return ResponseEntity.ok(employees);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable("id") Long employeeId,
                                                      @RequestBody EmployeeDto updatedEmployee) {
        EmployeeDto employeeDto = employeeService.updateEmployee(employeeId, updatedEmployee);
        return ResponseEntity.ok(employeeDto);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok("Employee deleted successfully.");
    }

    @PutMapping("{employeeId}/assign-role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping("/references")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<EmployeeReferenceDto>> getEmployeeReferences() {
        List<EmployeeReferenceDto> employees = employeeService.getEmployeeReferences();
        return ResponseEntity.ok(employees);
    }
}