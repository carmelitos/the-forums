package me.carmelo.theforums.repository.permission;

import me.carmelo.theforums.entity.Permission;
import me.carmelo.theforums.model.dto.PermissionDTO;

import java.util.List;

public interface IPermissionService {

    Permission getPermission(String permissionId);
    PermissionDTO mapToDTO(Permission permission);
    List<PermissionDTO> findAll();
}
