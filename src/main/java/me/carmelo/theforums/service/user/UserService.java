package me.carmelo.theforums.service.user;

import lombok.AllArgsConstructor;
import me.carmelo.theforums.entity.Role;
import me.carmelo.theforums.entity.User;
import me.carmelo.theforums.model.dto.UserDTO;
import me.carmelo.theforums.model.dto.UserRolesUpdateRequest;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import me.carmelo.theforums.repository.RoleRepository;
import me.carmelo.theforums.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDTO);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OperationResult<Long> createUser(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent())
            return new OperationResult<>(OperationStatus.FAILURE, "Username already exists", null);

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            return new OperationResult<>(OperationStatus.FAILURE, "Email already exists", null);

        User user = mapToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmailVerified(true); //let's enforce email verification since this method is only accessible to admins
        User savedUser = userRepository.save(user);
        return new OperationResult<>(OperationStatus.SUCCESS, "User created successfully", savedUser.getId());
    }

    @Override
    public OperationResult<Long> registerUser(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent())
            return new OperationResult<>(OperationStatus.FAILURE, "Username already exists", null);

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            return new OperationResult<>(OperationStatus.FAILURE, "Email already exists", null);

        User user = mapToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmailVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString()); // Generate a random token
        User savedUser = userRepository.save(user);

        // TODO send verification email by using an email handler or something like that

        return new OperationResult<>(OperationStatus.SUCCESS, "User created successfully. Email is unverified.", savedUser.getId());
    }


    @Override
    public OperationResult<Long> updateUser(Long id, UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty())
            return new OperationResult<>(OperationStatus.NOT_FOUND, "User with id " + id + " not found", null);
        User existingUser = userOptional.get();

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty())
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(existingUser);

        return new OperationResult<>(OperationStatus.SUCCESS, "User updated successfully.", existingUser.getId());
    }

    @Override
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id))
            return false;

        userRepository.deleteById(id);
        return true;
    }

    @Override
    public OperationResult<Long> manageRoleForUser(Long id, UserRolesUpdateRequest request) {

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty())
            return new OperationResult<>(OperationStatus.NOT_FOUND, "User with id " + id + " not found", null);
        User user = userOptional.get();

        Set<Role> roles = new HashSet<>();

        for (Long roleId : request.getRoleIds()) {
            Optional<Role> roleOptional = roleRepository.findById(roleId);
            if (roleOptional.isEmpty()) continue;
            roles.add(roleOptional.get());
        }

        return switch (request.getAction()) {
            case ADD -> {
                user.getRoles().addAll(roles);
                yield new OperationResult<>(OperationStatus.SUCCESS, "Roles added correctly", (long) roles.size());
            }

            case REMOVE -> {
                user.getRoles().removeAll(roles);
                yield new OperationResult<>(OperationStatus.SUCCESS, "Roles remove correctly", (long) roles.size());
            }

            case SET -> {
                user.setRoles(roles);
                yield new OperationResult<>(OperationStatus.SUCCESS, "Roles set correctly", (long) roles.size());
            }
        };
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        //we skip the password of course
        return dto;
    }

    private User mapToEntity(UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        // Password is handled separately (encoded before saving)
        return user;
    }
}
