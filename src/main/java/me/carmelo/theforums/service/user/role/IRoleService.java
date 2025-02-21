package me.carmelo.theforums.service.user.role;

import me.carmelo.theforums.entity.Role;
import me.carmelo.theforums.model.dto.RoleDTO;

public interface IRoleService {
    RoleDTO mapToDTO(Role role);
}
