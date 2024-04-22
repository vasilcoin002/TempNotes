package org.example.tempnotes.users;

import org.example.tempnotes.DTOs.RegisterRequest;
import org.example.tempnotes.DTOs.UserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping
    public ResponseEntity<?> getUser(@RequestBody UserRequest request) {
        try {
            return new ResponseEntity<>(userService.getUser(request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody RegisterRequest request) {
        try {
            return new ResponseEntity<>(userService.addUser(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestParam String id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>("user with id " + id + " has been successfully deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserRequest userBody) {
        try {
            return new ResponseEntity<>(userService.updateUser(userBody), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
