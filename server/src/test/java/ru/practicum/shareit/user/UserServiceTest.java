package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final long userId = 1;

    private final String updatedName = "updated Name";

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private final User user = User.builder()
            .id(userId)
            .name("User")
            .email("user@yandex.ru")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(userId)
            .name("User")
            .email("user@yandex.ru")
            .build();

    @BeforeEach
    void init() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getAllShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAll();

        verify(userRepository, times(1)).findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user, userService.getAll().get(0));
    }

    @Test
    void getByIdShouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getById(userId);

        verify(userRepository, times(1)).findById(userId);
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void getByIdShouldThrowUserNotFoundExceptionWhenRepositoryReturnEmpty() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void addShouldReturnUser() {
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.add(userDto);

        verify(userRepository, times(1)).save(user);
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void updateShouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(returnsFirstArg());

        userDto.setName(updatedName);
        User result = userService.update(userId, userDto);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any());
        assertNotNull(result);
        assertEquals(updatedName, result.getName());
    }

    @Test
    void updateShouldThrowUserNotFoundExceptionWhenRepositoryReturnEmpty() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(99L, userDto));
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void deleteShouldDelete() {
        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

}
