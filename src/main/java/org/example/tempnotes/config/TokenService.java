package org.example.tempnotes.config;

import lombok.RequiredArgsConstructor;
import org.example.tempnotes.tokens.Token;
import org.example.tempnotes.tokens.TokenRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public Token getToken(String token) {
        return tokenRepository.findByToken(token).orElseThrow(() -> new IllegalArgumentException("Token not found"));
    }
}
