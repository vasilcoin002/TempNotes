package org.example.tempnotes.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.tempnotes.DTOs.UserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("login")
    public ResponseEntity<?> login(
            HttpServletRequest httpServletRequest,
            @RequestBody UserRequest authenticationRequest
    ) {
        try {
            return new ResponseEntity<>(authenticationService.authenticate(httpServletRequest, authenticationRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("register")
    public ResponseEntity<?> register(
            HttpServletRequest httpServletRequest,
            @RequestBody UserRequest registerRequest
    ) {
        try {
            return new ResponseEntity<>(authenticationService.register(httpServletRequest ,registerRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
