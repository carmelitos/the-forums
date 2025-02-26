package me.carmelo.theforums.controller;

import lombok.RequiredArgsConstructor;
import me.carmelo.theforums.model.dto.PermissionDTO;
import me.carmelo.theforums.model.dto.RoleDTO;
import me.carmelo.theforums.repository.permission.IPermissionService;
import me.carmelo.theforums.service.role.IRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
public class PermissionController {

    private final IPermissionService permissionService;

    @GetMapping()
    @PreAuthorize("hasAuthority('PERMISSION_READ_PERMISSIONS')")
    public ResponseEntity<List<PermissionDTO>> getAll() {
        return ResponseEntity.ok(permissionService.findAll());
    }

}
