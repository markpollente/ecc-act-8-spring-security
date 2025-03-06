package com.markp.repository;

import com.markp.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EmployeeRepository extends BaseRepository<Employee, Long> {

    Employee findByEmailAndDeletedFalse(String email);

    boolean existsByEmailAndDeletedFalse(String email);

    @Query("SELECT e FROM Employee e WHERE e.deleted = false " +
            "AND (:email IS NULL OR e.email LIKE %:email%) " +
            "AND (:employmentStatus IS NULL OR e.employmentStatus LIKE %:employmentStatus%) " +
            "AND (cast(:createdDateStart as timestamp) IS NULL OR e.createdDate >= :createdDateStart) " +
            "AND (cast(:createdDateEnd as timestamp) IS NULL OR e.createdDate <= :createdDateEnd) " +
            "AND (cast(:updatedDateStart as timestamp) IS NULL OR e.updatedDate >= :updatedDateStart) " +
            "AND (cast(:updatedDateEnd as timestamp) IS NULL OR e.updatedDate <= :updatedDateEnd)")
    Page<Employee> findAllWithFilters(@Param("email") String email,
                                      @Param("employmentStatus") String employmentStatus,
                                      @Param("createdDateStart") LocalDateTime createdDateStart,
                                      @Param("createdDateEnd") LocalDateTime createdDateEnd,
                                      @Param("updatedDateStart") LocalDateTime updatedDateStart,
                                      @Param("updatedDateEnd") LocalDateTime updatedDateEnd,
                                      Pageable pageable);
}
