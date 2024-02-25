package org.example.tempnotes.users;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public User getUser(@RequestParam String id) {
        return userService.getUser(id);
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
