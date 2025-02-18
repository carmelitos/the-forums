package me.carmelo.theforums.controller;

import me.carmelo.theforums.model.dto.UserDTO;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import me.carmelo.theforums.service.user.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    // List all users - requires READ permission
    @PreAuthorize("hasAuthority('PERMISSION_READ_USER')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> listAllUsers() {
        List<UserDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // Get a user by ID - requires READ permission
    @PreAuthorize("hasAuthority('PERMISSION_READ_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        return (user != null)
                ? ResponseEntity.ok(user)
                : ResponseEntity.notFound().build();
    }

    // Create a new user - requires CREATE permission
    @PreAuthorize("hasAuthority('PERMISSION_CREATE_USER')")
    @PostMapping
    public ResponseEntity<OperationResult<Long>> createUser(@RequestBody UserDTO userDTO) {
        OperationResult<Long> result = userService.createUser(userDTO);
        if (result.getStatus() == OperationStatus.SUCCESS) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // Update an existing user - requires UPDATE permission
    @PreAuthorize("hasAuthority('PERMISSION_UPDATE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a user - requires DELETE permission
    @PreAuthorize("hasAuthority('PERMISSION_DELETE_USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update a user's password - requires UPDATE PASSWORD permission
    @PreAuthorize("hasAuthority('PERMISSION_UPDATE_PASSWORD')")
    @PatchMapping("/{id}/password")
    public ResponseEntity<UserDTO> updatePassword(@PathVariable Long id, @RequestBody String newPassword) {
        try {
            UserDTO updatedUser = userService.updatePassword(id, newPassword);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
