package me.carmelo.theforums.controller;

import me.carmelo.theforums.model.dto.*;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import me.carmelo.theforums.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_READ_USER')")
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_READ_USER')")
    public ResponseEntity<UserDTO> get(@PathVariable Long id) {
        return ResponseEntity.of(userService.findById(id));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('PERMISSION_READ_USER')")
    public ResponseEntity<Page<UserListItem>> search(@RequestBody UserSearchCriteria criteria) {
        Page<UserListItem> result = userService.searchUsers(criteria);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('PERMISSION_READ_USER')")
    public ResponseEntity<List<RoleDTO>> getRolesFromUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserRoles(id));
    }

    @PostMapping("/{id}/has-any-roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> userHasAnyRoles(@PathVariable Long id, @RequestBody List<String> roleNames) {
        boolean result = userService.userHasAnyRoles(id, roleNames);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_CREATE_USER')")
    public ResponseEntity<OperationResult<String>> create(@RequestBody UserDTO dto) {
        return handleResult(userService.validateAndSaveUser(dto, true), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_UPDATE_USER')")
    public ResponseEntity<OperationResult<String>> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        return handleResult(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DELETE_USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return userService.deleteUser(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("@securityChecker.hasRolePermission(#request.action)")
    public ResponseEntity<OperationResult<String>> manageRoles(
            @PathVariable Long id,
            @RequestBody UserRolesUpdateRequest request) {
        return handleResult(userService.manageRoleForUser(id, request));
    }

    private ResponseEntity<OperationResult<String>> handleResult(OperationResult<String> result) {
        return handleResult(result, HttpStatus.ACCEPTED);
    }

    private ResponseEntity<OperationResult<String>> handleResult(
            OperationResult<String> result,
            HttpStatus successStatus) {
        return ResponseEntity.status(result.getStatus() == OperationStatus.SUCCESS
                        ? successStatus
                        : HttpStatus.BAD_REQUEST)
                .body(result);
    }
}