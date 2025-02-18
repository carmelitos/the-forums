package me.carmelo.theforums.service.user;

import lombok.AllArgsConstructor;
import me.carmelo.theforums.entity.User;
import me.carmelo.theforums.model.dto.UserDTO;
import me.carmelo.theforums.model.enums.OperationStatus;
import me.carmelo.theforums.model.result.OperationResult;
import me.carmelo.theforums.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    @Override
    public UserDTO findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapToDTO)
                .orElse(null);
    }

    @Override
    public UserDTO findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToDTO)
                .orElse(null);
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
        User savedUser = userRepository.save(user);
        return new OperationResult<>(OperationStatus.SUCCESS, "User created successfully", savedUser.getId());
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty())
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User updatedUser = userRepository.save(existingUser);
        return mapToDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id))
            throw new RuntimeException("User not found with id " + id);

        userRepository.deleteById(id);
    }

    @Override
    public UserDTO updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
        user.setPassword(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
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
