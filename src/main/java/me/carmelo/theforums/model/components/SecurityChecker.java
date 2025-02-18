package me.carmelo.theforums.model.components;

import me.carmelo.theforums.model.enums.UserRolesUpdateAction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityChecker")
public class SecurityChecker {

    public boolean hasRolePermission(UserRolesUpdateAction action) {
        return switch (action) {
            case ADD -> hasAuthority("PERMISSION_ADD_ROLE_TO_USER");
            case REMOVE -> hasAuthority("PERMISSION_REMOVE_ROLE_FROM_USER");
            case SET -> hasAuthority("PERMISSION_SET_ROLES_TO_USER");
        };
    }

    private boolean hasAuthority(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals(permission));
    }
}