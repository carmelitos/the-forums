package me.carmelo.theforums.service.role;

import me.carmelo.theforums.entity.Permission;
import me.carmelo.theforums.entity.Role;
import me.carmelo.theforums.model.enums.DefaultPermission;
import me.carmelo.theforums.model.enums.DefaultRole;
import me.carmelo.theforums.repository.RoleRepository;
import me.carmelo.theforums.service.permission.IPermissionService;
import org.springframework.stereotype.Service;

@Service
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;
    private final IPermissionService permissionService;

    public RoleService(RoleRepository roleRepository, IPermissionService permissionService) {

        this.roleRepository = roleRepository;
        this.permissionService = permissionService;

        initDefaultRoles();
    }

    protected void initDefaultRoles() {
        for (DefaultRole defaultRole : DefaultRole.values()) {
            String roleName = defaultRole.name();
            if (roleRepository.findRoleByName(roleName).isPresent())
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

}
