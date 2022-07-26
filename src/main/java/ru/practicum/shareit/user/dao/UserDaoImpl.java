package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exception.EmailExistException;
import ru.practicum.shareit.error.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserDaoImpl implements UserDao {
    private long id = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(long userId) {
        checkId(userId);
        return users.get(userId);
    }

    @Override
    public User add(UserDto userDto) {
        checkExistEmail(userDto);
        long userId = getNextId();
        users.put(userId, UserMapper.toUser(userDto));
        users.get(userId).setId(userId);
        return users.get(userId);
    }

    @Override
    public User update(long userId, UserDto userDto) {
        checkId(userId);
        if (userDto.getName() != null) {
            users.get(userId).setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            checkExistEmail(userDto);
            users.get(userId).setEmail(userDto.getEmail());
        }
        return users.get(userId);
    }

    @Override
    public void delete(long userId) {
        checkId(userId);
        users.remove(userId);
    }

    private long getNextId() {
        return ++id;
    }

    private void checkId(long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("Пользователя с таким id: " + userId + " не существует");
        }
    }

    private void checkExistEmail(UserDto userDto) {
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            throw new EmailExistException("Пользователь с таким email: " + userDto.getEmail() + " уже существует");
        }
    }
}
