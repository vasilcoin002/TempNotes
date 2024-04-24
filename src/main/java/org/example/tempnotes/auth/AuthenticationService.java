package org.example.tempnotes.auth;

import lombok.RequiredArgsConstructor;
import org.example.tempnotes.DTOs.UserRequest;
import org.example.tempnotes.config.JwtService;
import org.example.tempnotes.DTOs.AuthenticationRequest;
import org.example.tempnotes.DTOs.AuthenticationResponse;
import org.example.tempnotes.DTOs.RegisterRequest;
import org.example.tempnotes.users.User;
import org.example.tempnotes.users.UserService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(@NonNull RegisterRequest request) {
        checkUserRequest(request);
        if (userService.isUserExistsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("user \"" + request.getEmail() + "\" already exists");
        }
        User user = userService.addUser(request);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(@NonNull AuthenticationRequest request) {
        checkUserRequest(request);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userService.getUserByEmail(request.getEmail());
        String token = jwtService.generateToken(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new AuthenticationResponse(token);
    }

    private void checkUserRequest(UserRequest request) {
        if (request.getEmail() == null) {
            throw new IllegalArgumentException("email is not provided");
        }
        if (request.getPassword() == null) {
            throw new IllegalArgumentException("password is not provided");
        }
    }

    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public void setAuthenticatedUser(User user) {
        if (
            user.getId() != null &&
            user.getEmail() != null &&
            user.getPassword() != null &&
            user.getRole() != null &&
            user.getNotesIdList() != null
        ) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities()
                    )
            );
        }
    }
}
