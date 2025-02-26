package me.carmelo.theforums.service.role;

import me.carmelo.theforums.entity.Role;
import me.carmelo.theforums.model.dto.RoleDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IRoleService {
    RoleDTO mapToDTO(Role role);
    List<RoleDTO> findAll();
}
