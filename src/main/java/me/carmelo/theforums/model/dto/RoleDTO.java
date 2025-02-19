package me.carmelo.theforums.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.carmelo.theforums.entity.Permission;

import java.util.List;

@Data
public class RoleDTO {

    private String name;
    private boolean isDefault;
    private List<PermissionDTO> permissions;

}
