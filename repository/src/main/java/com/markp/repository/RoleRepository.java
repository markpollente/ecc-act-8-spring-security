package com.markp.repository;

import com.markp.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends BaseRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.deleted = false")
    Page<Role> findAllActive(Pageable pageable);
}
