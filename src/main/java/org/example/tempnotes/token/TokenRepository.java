package org.example.tempnotes.token;

import org.example.tempnotes.users.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {
    List<Token> findAllValidTokenByUser(User user);

    Optional<Token> findByToken(String token);
}
