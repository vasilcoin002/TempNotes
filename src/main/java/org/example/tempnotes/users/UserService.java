package org.example.tempnotes.users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tempnotes.DTOs.AuthenticationResponse;
import org.example.tempnotes.DTOs.UserRequest;
import org.example.tempnotes.auth.AuthenticationService;
import org.example.tempnotes.devices.Device;
import org.example.tempnotes.tokens.Token;
import org.example.tempnotes.tokens.TokenAction;
import org.example.tempnotes.tokens.TokenRepository;
import org.example.tempnotes.tokens.TokenType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthenticationResponse updateUser(HttpServletRequest httpServletRequest, UserRequest request) {

        User user = authenticationService.getAuthenticatedUser();
        checkUpdateUserRequest(request, user);
        updateUserEmailAndPassword(request, user);

        List<Device> devices = user.getDevices();
        Device device = getDeviceFromListByName(httpServletRequest.getHeader("User-Agent"), devices);
        Token token = tokenRepository.save(
                Token.builder()
                        .token(authenticationService.generateToken(user))
                        .user(user)
                        .device(device)
                        .tokenType(TokenType.BEARER)
                        .tokenAction(TokenAction.ACCESS)
                    .build()
        );
        tokenRepository.deleteByToken(httpServletRequest.getHeader("Authorization").substring(7));

        userRepository.save(user);
        return new AuthenticationResponse(token.getToken());
    }

    public Device getDeviceFromListByName(String name, List<Device> devices) {
        return devices
                .stream()
                .filter(device -> Objects.equals(device.getName(), name)).findFirst()
                .orElseThrow(() -> new RuntimeException("Device is not found"));
    }

    private void checkUpdateUserRequest(UserRequest request, User user) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new RuntimeException("Data(email and password) is not provided");
        }

        if (user.getEmail().equals(request.getEmail()) &&
            passwordEncoder.matches(request.getPassword(), user.getPassword())
        ) {
            throw new RuntimeException("Provided the same email and password as user has");
        }

        if (!Objects.equals(user.getEmail(), request.getEmail()) &&
                        userRepository.existsByEmail(request.getEmail())
        ) {
            throw new RuntimeException("That email is taken. Try another");
        }
    }

    private void updateUserEmailAndPassword(UserRequest request, User user) {
        if (!user.getEmail().equals(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }
}
