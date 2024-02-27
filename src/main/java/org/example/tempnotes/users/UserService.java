package org.example.tempnotes.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
        return optionalUser.get();
    }

    public void addUser(UserBody userBody) {
        if (emailOrPasswordIsWrong(userBody.getEmail(), userBody.getPassword())) {
            throw new IllegalArgumentException("Email and password must be given");
        }
        User user = new User(userBody.getEmail(), userBody.getPassword(), new ArrayList<>());
        userRepository.save(user);
    }

    public void deleteUser(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must be given");
        }
        userRepository.deleteById(id);
    }

    public void updateUser(UserBody userBody) {
        if (userBody.getId() == null) {
            throw new IllegalArgumentException("Id must be given");
        }
        if (emailOrPasswordIsWrong(userBody.getEmail(), userBody.getPassword())) {
            throw new IllegalArgumentException("Email and password must be given");
        }
        User user = getUser(userBody.getId());
        user.setEmail(userBody.getEmail());
        user.setPassword(userBody.getPassword());
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    private boolean emailOrPasswordIsWrong(String email, String password) {
        return email == null || password == null;
    }
}
