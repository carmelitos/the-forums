package me.carmelo.theforums.service.user.permission;

import me.carmelo.theforums.entity.Permission;
import me.carmelo.theforums.model.dto.PermissionDTO;

public interface IPermissionService {

    Permission getPermission(String permissionId);
    PermissionDTO mapToDTO(Permission permission);
}
