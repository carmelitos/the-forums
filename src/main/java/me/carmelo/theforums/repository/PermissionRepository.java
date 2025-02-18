package me.carmelo.theforums.repository;

import me.carmelo.theforums.entity.Permission;
import me.carmelo.theforums.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findPermissionByName(String name);
}
