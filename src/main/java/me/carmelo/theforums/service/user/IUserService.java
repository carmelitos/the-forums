package me.carmelo.theforums.service.user;


import me.carmelo.theforums.entity.User;

import java.util.List;

public interface IUserService {

    User findById(Long id);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findAll();

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    User updatePassword(Long id, String newPassword);
}
