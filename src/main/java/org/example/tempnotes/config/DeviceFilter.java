package org.example.tempnotes.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DeviceFilter extends OncePerRequestFilter {

    private final DeviceService deviceService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        deviceService.checkRequestContainsDevice(request);
        String deviceName = request.getHeader("User-Agent");
        String accessTokenHeader = request.getHeader("Authorization");
        // TODO replace "" on refreshToken
        String refreshTokenHeader = "";

        if (authorizationTokensAreProvided(accessTokenHeader, refreshTokenHeader)) {
            accessTokenHeader = accessTokenHeader.substring(7);
            System.out.println(accessTokenHeader);
            deviceService.checkTokenBelongsDevice(accessTokenHeader, deviceName);
            // TODO uncomment when refresh token will be ready
//            refreshTokenHeader = refreshTokenHeader.substring(7);
//            deviceService.checkTokenBelongsDevice(refreshTokenHeader, deviceName);
        }
        filterChain.doFilter(request, response);
    }

    private boolean authorizationTokensAreProvided(String accessToken, String refreshToken) {
        return accessToken != null && refreshToken != null;
    }
}
