package me.carmelo.theforums.model.dto;

import lombok.Data;

@Data
public class UserSearchCriteria {
    private Long idFilter;           // Exact match on ID
    private String usernameFilter;   // Partial match on username
    private String emailFilter;      // Partial match on email
    private String phoneNumberFilter; // Partial match on phone number

    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String sortDirection = "ASC";
}
