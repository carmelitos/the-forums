package me.carmelo.theforums.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserListItem {

    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private Set<String> roles = new HashSet<>();
    private Set<String> permissions = new HashSet<>();

}
