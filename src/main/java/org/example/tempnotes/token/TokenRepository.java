package org.example.tempnotes.token;

import org.example.tempnotes.users.User;
import org.example.tempnotes.users.devices.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {
    List<Token> findAllValidTokenByUser(User user);

    List<Token> deleteAllByDevice(Device device);

    Optional<Token> findByToken(String token);
}
