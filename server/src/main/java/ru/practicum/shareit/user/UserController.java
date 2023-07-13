package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> add(@RequestBody UserDto userDto) {
        return ResponseEntity.ok().body(userService.add(userDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable int id,
                                          @RequestBody UserDto userDto) {
        return ResponseEntity.ok().body(userService.update(id, userDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable int id) {
        return ResponseEntity.ok().body(userService.get(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok().body(userService.getAll());
    }
}
