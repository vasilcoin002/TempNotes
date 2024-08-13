package org.example.tempnotes.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tempnotes.DTOs.AuthenticationResponse;
import org.example.tempnotes.DTOs.UserRequest;
import org.example.tempnotes.config.JwtService;
import org.example.tempnotes.devices.Device;
import org.example.tempnotes.devices.DeviceRepository;
import org.example.tempnotes.devices.DeviceStatus;
import org.example.tempnotes.tokens.Token;
import org.example.tempnotes.tokens.TokenAction;
import org.example.tempnotes.tokens.TokenRepository;
import org.example.tempnotes.tokens.TokenType;
import org.example.tempnotes.users.Role;
import org.example.tempnotes.users.User;
import org.example.tempnotes.users.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final DeviceRepository deviceRepository;

    private Token saveUserToken(User user, TokenAction tokenAction, Device device) {
        Token accessToken = Token.builder()
                .user(user)
                .token(jwtService.generateToken(user))
                .tokenType(TokenType.BEARER)
                .tokenAction(tokenAction)
                .device(device)
                .build();
        tokenRepository.save(accessToken);
        return accessToken;
    }

    @Transactional
    public AuthenticationResponse register(
            @NonNull HttpServletRequest httpServletRequest,
            @NonNull UserRequest request
    ) {
        String deviceName = httpServletRequest.getHeader("User-Agent");
        User user = userRepository.save(
                User.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.USER)
                    .build()
        );
        Device device = deviceRepository.save(
                Device.builder()
                        .name(deviceName)
                        .status(DeviceStatus.ACCEPTED)
                        .user(user)
                    .build()
        );
        Token accessToken = tokenRepository.save(
                Token.builder()
                        .user(user)
                        .token(jwtService.generateToken(user))
                        .tokenType(TokenType.BEARER)
                        .tokenAction(TokenAction.ACCESS)
                        .device(device)
                    .build()
        );
        return new AuthenticationResponse(accessToken.getToken());
    }

    // TODO create the accept device method
    @Transactional
    public AuthenticationResponse authenticate(
            @NonNull HttpServletRequest httpServletRequest,
            @NonNull UserRequest request
    ) {
        checkUserRequest(request);
        checkHttpServletRequest(httpServletRequest);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(
                request.getEmail()).orElseThrow(
                        () -> new UsernameNotFoundException("user " + request.getEmail() + " not found")
                );
        List<Device> userDevices = user.getDevices();
        String deviceName = httpServletRequest.getHeader("User-Agent");
        Device userDevice = getUserDeviceByName(user, deviceName);
        if (!getDevicesNames(userDevices).contains(deviceName)) {
            userDevice = deviceRepository.save(
                    Device.builder()
                                .name(deviceName)
                                .status(DeviceStatus.NOT_ACCEPTED)
                                .user(user)
                            .build()
            );
            userDevices.add(userDevice);
            setAuthenticatedUser(user);
            System.out.println(3);
        } else {
            tokenRepository.deleteAllByDevice(userDevice);
        }
        Token accessToken = tokenRepository.save(
                Token.builder()
                            .user(user)
                            .token(jwtService.generateToken(user))
                            .tokenType(TokenType.BEARER)
                            .tokenAction(TokenAction.ACCESS)
                            .device(userDevice)
                        .build()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new AuthenticationResponse(accessToken.getToken());

    }

    public Device getUserDeviceByName(User user, String deviceName) {
        List<Device> userDevices = user.getDevices();
        return userDevices.stream()
                .filter(
                        device -> Objects.equals(
                                deviceName,
                                device.getName()
                        )
                )
                .findFirst()
                .orElse(null);
    }

    private List<String> getDevicesNames(List<Device> devices) {
        return devices.stream().map(Device::getName).collect(Collectors.toList());
    }

    private void checkUserRequest(UserRequest request) {
        if (request.getEmail() == null) {
            throw new IllegalArgumentException("Email is not provided");
        }
        if (request.getPassword() == null) {
            throw new IllegalArgumentException("Password is not provided");
        }
    }

    private void checkHttpServletRequest(HttpServletRequest request) {
        String deviceName = request.getHeader("User-Agent");
        if (deviceName == null) {
            throw new IllegalArgumentException("User-Agent must be in headers");
        }
    }

    public String generateToken(UserDetails userDetails) {
        return jwtService.generateToken(userDetails);
    }

    @Transactional
    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public void setAuthenticatedUser(User user) {
        if (
                user.getId() != null &&
                        user.getEmail() != null &&
                        user.getPassword() != null &&
                        user.getRole() != null
        ) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            user.getPassword()
                    )
            );
        }
    }
}
