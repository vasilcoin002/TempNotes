package org.example.tempnotes.users;

import lombok.RequiredArgsConstructor;
import org.example.tempnotes.auth.RegisterRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUser(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.orElseThrow(() -> new UsernameNotFoundException("User with id " + id + " not found"));
        return optionalUser.get();
    }

    public User getUser(UserRequest request) {
        if (request.getEmail() == null) {
            throw new UsernameNotFoundException("email is not provided");
        }
        return getUserByEmail(request.getEmail());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("user " + email + " not found"));
    }

    public User addUser(RegisterRequest request) {
        if (emailOrPasswordIsWrong(request.getEmail(), request.getPassword())) {
            throw new IllegalArgumentException("Email and password must be given");
        }
        return userRepository.save(
                User.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .notesIdList(new ArrayList<>())
                        .role(Role.USER)
                    .build()
        );
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

    public boolean isUserExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private boolean emailOrPasswordIsWrong(String email, String password) {
        return email == null || password == null;
    }
}
