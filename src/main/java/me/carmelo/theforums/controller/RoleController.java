package me.carmelo.theforums.controller;

import lombok.RequiredArgsConstructor;
import me.carmelo.theforums.model.dto.RoleDTO;
import me.carmelo.theforums.service.role.IRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    @GetMapping()
    @PreAuthorize("hasAuthority('PERMISSION_READ_ROLES')")
    public ResponseEntity<List<RoleDTO>> getAll() {
        return ResponseEntity.ok(roleService.findAll());
    }
}
