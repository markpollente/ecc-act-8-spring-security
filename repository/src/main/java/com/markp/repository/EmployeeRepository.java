package com.markp.repository;

import com.markp.model.Employee;

public interface EmployeeRepository extends BaseRepository<Employee, Long> {

    Employee findByEmailAndDeletedFalse(String email);

    boolean existsByEmailAndDeletedFalse(String email);
}
