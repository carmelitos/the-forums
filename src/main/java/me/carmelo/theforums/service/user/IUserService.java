package me.carmelo.theforums.service.user;

import me.carmelo.theforums.model.dto.UserDTO;
import me.carmelo.theforums.model.result.OperationResult;

import java.util.List;

public interface IUserService {

    UserDTO findById(Long id);

    UserDTO findByUsername(String username);

    UserDTO findByEmail(String email);

    List<UserDTO> findAll();

    OperationResult<Long> createUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);

    UserDTO updatePassword(Long id, String newPassword);
}
