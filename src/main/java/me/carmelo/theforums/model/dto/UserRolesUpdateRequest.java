package me.carmelo.theforums.model.dto;

import lombok.Data;
import me.carmelo.theforums.model.enums.UserRolesUpdateAction;

import java.util.List;

@Data
public class UserRolesUpdateRequest {

    private int userId;
    private List<Long> roleIds;
    private UserRolesUpdateAction action;

}
