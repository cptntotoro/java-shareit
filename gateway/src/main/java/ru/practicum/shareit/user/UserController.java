package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid UserDto userDto) {

        log.info("Add user with userDto={}", userDto);

        return userClient.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable @Positive int userId,
                                         @RequestBody @Valid UserDto userDto) {

        log.info("Update user with userId={}, userDto={}", userId, userDto);

        return userClient.update(userDto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable @Positive int userId) {

        log.info("Get user by userId={}", userId);

        return userClient.get(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable @Positive int userId) {

        log.info("Delete user by userId={}", userId);

        return userClient.delete(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {

        log.info("Get all users");

        return userClient.getAll();
    }
}
