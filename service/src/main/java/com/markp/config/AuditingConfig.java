package com.markp.config;

import com.markp.repository.EmployeeRepository;
import com.markp.service.impl.AuditorAwareImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditingConfig {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public AuditingConfig(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl(employeeRepository);
    }
}