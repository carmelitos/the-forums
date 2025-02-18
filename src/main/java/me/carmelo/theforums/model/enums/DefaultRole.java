package me.carmelo.theforums.model.enums;

import lombok.Getter;

@Getter
public enum DefaultRole {

    ADMIN(
            DefaultPermission.CREATE_USER,
            DefaultPermission.UPDATE_USER,
            DefaultPermission.DELETE_USER,
            DefaultPermission.READ_USER
    ),
    USER;

    DefaultRole(DefaultPermission... defaultPermission) {
        this.permissions = defaultPermission;
    }

    private final DefaultPermission[] permissions;

}

