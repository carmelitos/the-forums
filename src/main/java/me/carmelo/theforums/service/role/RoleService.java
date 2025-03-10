package me.carmelo.theforums.service.role;

import me.carmelo.theforums.entity.Permission;
import me.carmelo.theforums.entity.Role;
import me.carmelo.theforums.model.dto.RoleDTO;
import me.carmelo.theforums.model.enums.DefaultPermission;
import me.carmelo.theforums.model.enums.DefaultRole;
import me.carmelo.theforums.repository.RoleRepository;
import me.carmelo.theforums.repository.permission.IPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;
    private final IPermissionService permissionService;

    public RoleService(RoleRepository roleRepository, IPermissionService permissionService) {

        this.roleRepository = roleRepository;
        this.permissionService = permissionService;

        initDefaultRoles();
    }

    public List<RoleDTO> findAll() {
        return roleRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<RoleDTO> findById(Long id) {
        return roleRepository.findById(id).map(this::mapToDTO);
    }

    public Optional<RoleDTO> findById(String name) {
        return roleRepository.findByName(name).map(this::mapToDTO);
    }

    protected void initDefaultRoles() {
        for (DefaultRole defaultRole : DefaultRole.values()) {
            String roleName = defaultRole.name();
            if (roleRepository.findByName(roleName).isPresent())
                continue;

            Role role = new Role();
            role.setDefault(true);
            role.setName(roleName);

            for(DefaultPermission defaultPermission : defaultRole.getPermissions()) {
                Permission permission = permissionService.getPermission(defaultPermission.name());
                role.getPermissions().add(permission);
            }

            roleRepository.save(role);
        }

    }

    public RoleDTO mapToDTO(Role role) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName(role.getName());
        roleDTO.setDefault(role.isDefault());
        roleDTO.setPermissions(role.getPermissions().stream().map(permissionService::mapToDTO).collect(Collectors.toList()));
        return roleDTO;
    }

}
