package org.example.tempnotes.auth;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.tempnotes.config.JwtService;
import org.example.tempnotes.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        UserBody userBody = new UserBody(
                null,
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        User user = userService.addUser(userBody);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userService.getUserByEmail(request.getEmail());
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
