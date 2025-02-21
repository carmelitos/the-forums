package me.carmelo.theforums.repository.permission;

import me.carmelo.theforums.entity.Permission;
import me.carmelo.theforums.model.dto.PermissionDTO;
import me.carmelo.theforums.model.enums.DefaultPermission;
import me.carmelo.theforums.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
        initDefaultRoles();
    }

    public Optional<PermissionDTO> findById(Long id) {
        return permissionRepository.findById(id).map(this::mapToDTO);
    }

    public Optional<PermissionDTO> findById(String name) {
        return permissionRepository.findPermissionByName(name).map(this::mapToDTO);
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

    public PermissionDTO mapToDTO(Permission permission) {
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setName(permission.getName());
        permissionDTO.setDefault(permission.isDefault());
        return permissionDTO;
    }
}
