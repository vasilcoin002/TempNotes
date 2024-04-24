package org.example.tempnotes.users;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.tempnotes.DTOs.RegisterRequest;
import org.example.tempnotes.DTOs.UserRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return (User) securityContext.getAuthentication().getPrincipal();
    }

    public User getUser(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.orElseThrow(() -> new UsernameNotFoundException("User with id " + id + " not found"));
        return optionalUser.get();
    }

    public User getUser(UserRequest request) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = (User) securityContext.getAuthentication().getPrincipal();

        if (request.getEmail() == null) {
            throw new UsernameNotFoundException("email is not provided");
        }
        return getUserByEmail(request.getEmail());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("user " + email + " not found"));
    }

    public User addUser(UserRequest request) {
        checkUserRequest(request);
        return userRepository.save(
                User.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .notesIdList(new ArrayList<>())
                        .role(Role.USER)
                        .build()
        );
    }

    public User updateUser(UserRequest request) {
        checkUserRequest(request);
        User user = getUser(request);
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user = userRepository.save(user);
        return user;
    }

    public User updateUser(User user) {
        checkUser(user);
        return userRepository.save(user);
    }

    public boolean isUserExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private void checkUser(User user) {
        UserRequest request = UserRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        checkUserRequest(request);
    }

    private void checkUserRequest(UserRequest request) {
        if (request.getEmail() == null) {
            throw new IllegalArgumentException("email is not provided");
        }
        if (request.getPassword() == null) {
            throw new IllegalArgumentException("password is not provided");
        }
    }
}
