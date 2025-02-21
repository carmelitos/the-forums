package me.carmelo.theforums.service.user;

import me.carmelo.theforums.model.dto.RoleDTO;
import me.carmelo.theforums.model.dto.UserDTO;
import me.carmelo.theforums.model.dto.UserRolesUpdateRequest;
import me.carmelo.theforums.model.result.OperationResult;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    Optional<UserDTO> findById(Long id);
    Optional<UserDTO> findByUsername(String username);
    List<UserDTO> findAll();

    boolean hasVerifiedEmail(String username);

    OperationResult<String> validateAndSaveUser(UserDTO userDTO, boolean isAdminCreated);
    OperationResult<String> updateUser(Long id, UserDTO userDTO);

    boolean deleteUser(Long id);

    List<RoleDTO> getUserRoles(Long userId);
    OperationResult<String> manageRoleForUser(Long id, UserRolesUpdateRequest request);

    String createSuperUser();
}
