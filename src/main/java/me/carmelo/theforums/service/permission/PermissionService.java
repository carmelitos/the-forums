package me.carmelo.theforums.service.permission;

import me.carmelo.theforums.entity.Permission;
import me.carmelo.theforums.model.enums.DefaultPermission;
import me.carmelo.theforums.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
        initDefaultRoles();
    }


    private void initDefaultRoles() {
        for (DefaultPermission defaultPermission : DefaultPermission.values()) {
            String permissionName = defaultPermission.name();
            if (permissionRepository.findPermissionByName(permissionName).isPresent())
                continue;

            Permission permission = new Permission();
            permission.setDefault(true);
            permission.setName(permissionName);

            permissionRepository.save(permission);
        }
    }

    public Permission getPermission(String permissionName) {
        return permissionRepository.findPermissionByName(permissionName).orElse(null);
    }
}
