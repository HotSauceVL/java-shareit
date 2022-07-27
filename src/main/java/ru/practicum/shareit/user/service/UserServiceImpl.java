package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public List<UserDto> getAll() {
        return userDao.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long userId) {
        return UserMapper.toUserDto(userDao.getById(userId));
    }

    @Override
    public UserDto add(UserDto userDto) {
        return UserMapper.toUserDto(userDao.add(userDto));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        return UserMapper.toUserDto(userDao.update(userId, userDto));
    }

    @Override
    public void delete(long userId) {
        userDao.delete(userId);
    }
}
