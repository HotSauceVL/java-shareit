package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@EntityScan("ru.practicum.shareit.user.dao")
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public User getById(long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id: " + userId + " не существует"));
    }

    @Transactional
    @Override
    public User add(UserDto userDto) {
        return repository.save(UserMapper.toUser(userDto));
    }

    @Override
    public User update(long userId, UserDto userDto) {
        User user = getById(userId);
        userDto.setId(userId);
        User updatedUser = UserMapper.toUser(userDto);
        if (updatedUser.getEmail() == null) {
            updatedUser.setEmail(user.getEmail());
        }
        if (updatedUser.getName() == null) {
            updatedUser.setName(user.getName());
        }
        return repository.save(updatedUser);
    }

    @Override
    public void delete(long userId) {
        repository.deleteById(userId);
    }
}
