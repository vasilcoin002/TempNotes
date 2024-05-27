package org.example.tempnotes.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.tempnotes.token.Token;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final TokenService tokenService;

    public boolean tokenBelongsDevice(Token token, String deviceName) {
        return Objects.equals(token.getDevice().getName(), deviceName);
    }

    public boolean tokenBelongsDevice(@NonNull String token, String deviceName) {
        Token tokenObj = tokenService.getToken(token);
        return tokenBelongsDevice(tokenObj, deviceName);
    }

    public void checkTokenBelongsDevice(@NonNull String token, @NonNull String deviceName) {
        if (!tokenBelongsDevice(token, deviceName)) {
            throw new IllegalArgumentException("Token doesn't belong to this device");
        }
    }

    public void checkRequestContainsDevice(@NonNull HttpServletRequest request) {
        String deviceName = request.getHeader("User-Agent");
        if (deviceName == null) {
            throw new IllegalArgumentException("User-Agent header must be provided");
        }
    }

//    public void checkRequestContainsAccessToken(@NonNull HttpServletRequest request) {
//        String accessTokenHeader = request.getHeader("Authorization");
//        if (accessTokenHeader == null || !accessTokenHeader.startsWith("Bearer ")) {
//            throw new IllegalArgumentException("Authorization header must be provided");
//        }
//    }
//
//    public void checkRequestContainsRefreshToken(@NonNull HttpServletRequest request) {
//        String accessTokenHeader = request.getHeader("Refresh");
//        if (accessTokenHeader == null || !accessTokenHeader.startsWith("Bearer ")) {
//            throw new IllegalArgumentException("Refresh header must be provided");
//        }
//    }
}
