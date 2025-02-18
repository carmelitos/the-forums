package me.carmelo.theforums.service.user;

import me.carmelo.theforums.entity.User;
import me.carmelo.theforums.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Map roles to authorities (prefixed with "ROLE_")
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            // Also include permissions that come with the role (prefixed with "PERMISSION_")
            role.getPermissions().forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority("PERMISSION_" + permission.getName()))
            );
        });

        // Map any permissions that are directly assigned to the user
        user.getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority("PERMISSION_" + permission.getName()))
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
