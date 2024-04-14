package org.example.tempnotes.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    
    public User getUser(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.orElseThrow(() -> new UsernameNotFoundException("User with id " + id + " not found"));
        return optionalUser.get();
    }

    public User getUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        optionalUser.orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
        return optionalUser.get();
    }

    public User addUser(UserBody userBody) {
        if (emailOrPasswordIsWrong(userBody.getEmail(), userBody.getPassword())) {
            throw new IllegalArgumentException("Email and password must be given");
        }
        User user = new User(userBody.getEmail(), userBody.getPassword(), new ArrayList<>(), Role.USER);
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must be given");
        }
        userRepository.deleteById(id);
    }

    public User updateUser(UserBody userBody) {
        if (userBody.getId() == null) {
            throw new IllegalArgumentException("Id must be given");
        }
        if (emailOrPasswordIsWrong(userBody.getEmail(), userBody.getPassword())) {
            throw new IllegalArgumentException("Email and password must be given");
        }
        User user = getUser(userBody.getId());
        user.setEmail(userBody.getEmail());
        user.setPassword(userBody.getPassword());
        user = userRepository.save(user);
        return user;
    }

    public User updateUser(User user) {
        try {
            user = userRepository.save(user);
            return user;
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean emailOrPasswordIsWrong(String email, String password) {
        return email == null || password == null;
    }
}
