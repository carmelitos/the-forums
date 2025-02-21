package me.carmelo.theforums.repository.permission;

import me.carmelo.theforums.entity.Permission;
import me.carmelo.theforums.model.dto.PermissionDTO;

public interface IPermissionService {

    Permission getPermission(String permissionId);
    PermissionDTO mapToDTO(Permission permission);
}
