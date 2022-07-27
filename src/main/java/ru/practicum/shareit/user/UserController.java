package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validate.OnCreate;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        return userService.getById(userId);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @Valid @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userService.delete(userId);
    }


}
