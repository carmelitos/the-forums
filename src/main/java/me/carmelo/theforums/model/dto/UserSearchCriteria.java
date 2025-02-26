package me.carmelo.theforums.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserSearchCriteria {
    private Long idFilter;
    private String usernameFilter;
    private String emailFilter;
    private String phoneNumberFilter;

    private List<String> rolesFilter;
    private List<String> permissionsFilter;

    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String sortDirection = "ASC";
}
