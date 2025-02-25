package me.carmelo.theforums.model.dto;

import lombok.Data;

@Data
public class UserListItem {

    private Long id;
    private String username;
    private String email;
    private String phoneNumber;

}
