package linx7a.user_pets_spring_mvc.controller;

import jakarta.validation.Valid;
import linx7a.user_pets_spring_mvc.model.UserDto;
import linx7a.user_pets_spring_mvc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody UserDto userToCreate
    ) {
        log.info("Post request to create user: user={}", userToCreate);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDto userToUpdate
    ) {
        log.info("Put request to update user by id={}, userToUpdate={}", id, userToUpdate);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateById(id, userToUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id
    ) {
        log.info("Delete request to delete user by id={}", id);
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable Long id
    ) {
        log.info("Get request to get user by id={}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getById(id));
    }
}
