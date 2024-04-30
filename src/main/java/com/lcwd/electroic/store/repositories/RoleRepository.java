package com.lcwd.electroic.store.repositories;

import com.lcwd.electroic.store.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
