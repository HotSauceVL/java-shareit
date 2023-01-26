package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll(HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI());
        return userService.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId, HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, параметр пути запроса {}", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(), userId);
        return UserMapper.toUserDto(userService.getById(userId));
    }

    @PostMapping
    @Validated
    public UserDto add(@RequestBody UserDto userDto, HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(), userDto);
        return UserMapper.toUserDto(userService.add(userDto));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UserDto userDto,
                          HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, параметр пути запроса {}, тело запроса {}",
                httpServletRequest.getMethod(),httpServletRequest.getRequestURI(), userId, userDto);
        return UserMapper.toUserDto(userService.update(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId, HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, параметр пути запроса {}", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(), userId);
        userService.delete(userId);
    }

}
