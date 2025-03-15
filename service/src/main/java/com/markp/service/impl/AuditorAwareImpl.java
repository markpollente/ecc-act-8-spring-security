package com.markp.service.impl;

import com.markp.model.Employee;
import com.markp.repository.EmployeeRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    private final EmployeeRepository employeeRepository;

    public AuditorAwareImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UsernamePasswordAuthenticationToken authenticationToken) {
            username = authenticationToken.getName();
        } else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User user) {
            username = user.getUsername();
        } else if (authentication.getPrincipal() instanceof String principal) {
            username = principal;
        }

        if (username != null) {
            Employee employee = employeeRepository.findByEmailAndDeletedFalse(username);
            if (employee != null) {
                return Optional.of(employee.getFirstName() + " " + employee.getLastName());
            }
        }

        return Optional.empty();
    }
}