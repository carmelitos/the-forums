package me.carmelo.theforums.service.role;

import me.carmelo.theforums.entity.Role;
import me.carmelo.theforums.model.dto.RoleDTO;

public interface IRoleService {
    RoleDTO mapToDTO(Role role);
}
