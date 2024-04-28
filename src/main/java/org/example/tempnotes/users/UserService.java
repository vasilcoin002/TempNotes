package org.example.tempnotes.users;

import lombok.RequiredArgsConstructor;
import org.example.tempnotes.DTOs.AuthenticationResponse;
import org.example.tempnotes.DTOs.UserRequest;
import org.example.tempnotes.auth.AuthenticationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    public AuthenticationResponse updateUser(UserRequest request) {
        User user = getAuthenticatedUser();
        checkUpdateUserRequest(request, user);

        user = userRepository.save(user);
        setAuthenticatedUser(user);
        String token = authenticationService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public List<String> updateUserNotesIdList(List<String> notesIdList) {
        User user = getAuthenticatedUser();
        user.setNotesIdList(notesIdList);
        user = userRepository.save(user);
        setAuthenticatedUser(user);
        return user.getNotesIdList();
    }

    // TODO refactor checkUpdateUserRequest method
    private void checkUpdateUserRequest(UserRequest request, User user) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("data(email and password) is not provided");
        }
        if (
                user.getEmail().equals(request.getEmail()) &&
                passwordEncoder.matches(request.getPassword(), user.getPassword())
        ) {
            throw new IllegalArgumentException("Provided the same email and password as user has");
        }
        if (
                !Objects.equals(user.getEmail(), request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())
        ) {
            throw new IllegalArgumentException("That email is taken. Try another");
        }
        if (!user.getEmail().equals(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }
}
