package me.carmelo.theforums.service.user;

import me.carmelo.theforums.model.dto.*;
import me.carmelo.theforums.model.result.OperationResult;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    Optional<UserDTO> findById(Long id);
    Optional<UserDTO> findByUsername(String username);
    Long getUserId(String username);
    List<UserDTO> findAll();

    boolean hasVerifiedEmail(String username);

    OperationResult<String> validateAndSaveUser(UserDTO userDTO, boolean isAdminCreated);
    OperationResult<String> updateUser(Long id, UserDTO userDTO);

    boolean deleteUser(Long id);

    boolean userHasAnyRoles(Long userId, List<String> roleNames);
    List<RoleDTO> getUserRoles(Long userId);
    OperationResult<String> manageRoleForUser(Long id, UserRolesUpdateRequest request);

    String createSuperUser();

    Page<UserListItem> searchUsers(UserSearchCriteria criteria);
}
