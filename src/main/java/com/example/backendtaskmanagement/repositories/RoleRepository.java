package com.example.backendtaskmanagement.repositories;

import com.example.backendtaskmanagement.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}