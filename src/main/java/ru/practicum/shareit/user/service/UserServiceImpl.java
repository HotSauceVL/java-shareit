package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ComponentScan({"ru.practicum.shareit.user.dao"})
@EntityScan("ru.practicum.shareit.user.dao")
//@EnableJpaRepositories("ru.practicum.shareit.user.dao")
public class UserServiceImpl implements UserService {

    //private final UserDao userDao;
    private final UserRepository repository;

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList()); //userDao.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long userId) {
        return UserMapper.toUserDto(repository.findById(userId).get());//UserMapper.toUserDto(userDao.getById(userId));
    }

    @Override
    public UserDto add(UserDto userDto) {
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(userDto)));//userDao.add(userDto));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        userDto.setId(userId);
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(userDto)));// userDao.update(userId, userDto));
    }

    @Override
    public void delete(long userId) {
        repository.deleteById(userId);
        //userDao.delete(userId);
    }
}
