package com.markp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {

        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            if (authentication.getPrincipal() instanceof UsernamePasswordAuthenticationToken authenticationToken) {
                return Optional.of(authenticationToken.getName());
            }

            if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User user) {
                return Optional.of(user.getUsername());
            }

            if(authentication.getPrincipal() instanceof String principal) {
                return Optional.of(principal);
            }

            return Optional.empty();
        };
    }

}