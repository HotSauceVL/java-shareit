package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User getById(long userId);

    User add(UserDto userDto);

    User update(long userId, UserDto userDto);

    void delete(long userId);

}
