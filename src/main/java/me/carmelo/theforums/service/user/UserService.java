package me.carmelo.theforums.service.user;

import lombok.RequiredArgsConstructor;
import me.carmelo.theforums.entity.Role;
import me.carmelo.theforums.entity.User;
import me.carmelo.theforums.model.dto.RoleDTO;
import me.carmelo.theforums.model.dto.UserDTO;
import me.carmelo.theforums.model.dto.UserRolesUpdateRequest;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import me.carmelo.theforums.repository.RoleRepository;
import me.carmelo.theforums.repository.UserRepository;
import me.carmelo.theforums.service.role.IRoleService;
import me.carmelo.theforums.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final IRoleService roleService;
    private final JwtUtil jwtUtil;

    @Override
    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::mapToDTO);
    }

    @Override
    public boolean hasVerifiedEmail(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) return false;
        User user = userOptional.get();
        return user.isEmailVerified();
    }


    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OperationResult<String> updateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(existingUser -> updateUserDetails(existingUser, userDTO))
                .orElse(new OperationResult<>(OperationStatus.NOT_FOUND, "User not found", "User not found"));
    }

    @Override
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public OperationResult<String> manageRoleForUser(Long id, UserRolesUpdateRequest request) {
        return userRepository.findById(id)
                .map(user -> updateUserRoles(user, request))
                .orElse(new OperationResult<>(OperationStatus.NOT_FOUND, "User not found", "User not found"));
    }

    @Override
    public List<RoleDTO> getUserRoles(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;
        return user.getRoles().stream().map(roleService::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public OperationResult<String> validateAndSaveUser(UserDTO userDTO, boolean isAdminCreated) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent())
            return new OperationResult<>(OperationStatus.FAILURE, "Username already exists", "username already exists");
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            return new OperationResult<>(OperationStatus.FAILURE, "Email already exists", "email already exists");

        User user = mapToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmailVerified(isAdminCreated);

        //persist
        userRepository.save(user);

        String message = isAdminCreated
                ? "User created successfully"
                : "User created successfully. Email is unverified.";
        String data = isAdminCreated
                ? "User created successfully"
                : "Verification token is now handled via Redis and AuthService.";

        return new OperationResult<>(OperationStatus.SUCCESS, message, data);
    }

    private OperationResult<String> updateUserDetails(User user, UserDTO userDTO) {
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty())
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);
        return new OperationResult<>(OperationStatus.SUCCESS, "User updated successfully", "User updated successfully");
    }

    private OperationResult<String> updateUserRoles(User user, UserRolesUpdateRequest request) {
        Set<Role> roles = request.getRoleIds().stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        switch (request.getAction()) {
            case ADD -> user.getRoles().addAll(roles);
            case REMOVE -> user.getRoles().removeAll(roles);
            case SET -> user.setRoles(roles);
        }

        userRepository.save(user);
        return new OperationResult<>(OperationStatus.SUCCESS, "Roles updated successfully", "Roles updated successfully");
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }

    private User mapToEntity(UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        return user;
    }
}