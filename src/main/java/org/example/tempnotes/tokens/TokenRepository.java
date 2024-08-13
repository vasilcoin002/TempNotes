package org.example.tempnotes.tokens;

import org.example.tempnotes.devices.Device;
import org.example.tempnotes.tokens.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    public void deleteAllByDevice(Device device);
    public void deleteByToken(String token);
    public Optional<Token> findByToken(String token);
}
