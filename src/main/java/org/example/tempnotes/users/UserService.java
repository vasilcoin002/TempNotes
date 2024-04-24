package org.example.tempnotes.users;

import lombok.RequiredArgsConstructor;
import org.example.tempnotes.DTOs.UserRequest;
import org.example.tempnotes.auth.AuthenticationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    public User getAuthenticatedUser() {
        return authenticationService.getAuthenticatedUser();
    }

    public void setAuthenticatedUser(User user) {
        authenticationService.setAuthenticatedUser(user);
    }

    public void addUser(UserRequest request) {
        authenticationService.register(request);
    }

    public User updateUser(UserRequest request) {
        checkUserRequest(request);
        User user = getAuthenticatedUser();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        authenticationService.setAuthenticatedUser(user);
        return user;
    }

    public User updateUser(User user) {
        checkUser(user);
        return updateUser(new UserRequest(user.getEmail(), user.getPassword()));
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
