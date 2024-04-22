package org.example.tempnotes.auth;

import lombok.RequiredArgsConstructor;
import org.example.tempnotes.config.JwtService;
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
        if (userService.isUserExistsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User \"" + request.getEmail() + "\" already exists");
        }
        User user = userService.addUser(request);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(@NonNull AuthenticationRequest request) {
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
}
