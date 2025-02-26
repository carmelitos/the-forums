package me.carmelo.theforums.model.enums;

import lombok.Getter;

@Getter
public enum DefaultRole {

    ADMIN(
            DefaultPermission.values()
    ),
    USER;

    DefaultRole(DefaultPermission... defaultPermission) {
        this.permissions = defaultPermission;
    }

    private final DefaultPermission[] permissions;

}

