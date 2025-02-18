package me.carmelo.theforums.service.user;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import me.carmelo.theforums.entity.User;
import me.carmelo.theforums.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public User findByEmail(String email) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public User updateUser(Long id, User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteUser(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public User updatePassword(Long id, String newPassword) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
