package org.example.tempnotes.users;

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

    @GetMapping
    public ResponseEntity<?> getUser(@RequestParam String id) {
        try {
            return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public void addUser(@RequestBody UserBody userBody) {
        userService.addUser(userBody);
    }

    @DeleteMapping
    public void deleteUser(@RequestParam String id) {
        userService.deleteUser(id);
    }

    @PutMapping
    public void updateUser(@RequestBody UserBody userBody) {
        userService.updateUser(userBody);
    }
}
