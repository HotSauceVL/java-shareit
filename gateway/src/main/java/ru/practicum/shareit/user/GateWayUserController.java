package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GateWayUserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Get all users");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable @PositiveOrZero long userId) {
        log.info("Get user by id {}", userId);
        return userClient.getById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody UserDto userDto) {
        log.info("Add user {}", userDto);
        return userClient.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable @PositiveOrZero long userId, @RequestBody UserDto userDto) {
        log.info("Update user by id {}, user {}", userId, userDto);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable @PositiveOrZero long userId) {
        log.info("Delete user by id {}", userId);
        return userClient.delete(userId);
    }

}
