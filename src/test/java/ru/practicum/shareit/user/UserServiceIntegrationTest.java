package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    private final UserService userService;

    private final UserDto userDto = UserDto.builder()
            .name("User")
            .email("User@yandex.ru").build();

    private final UserDto anotherUserDto = UserDto.builder()
            .name("Another user")
            .email("AnotherUser@yandex.ru").build();


    @Test
    void getByIdShouldReturnUser() {
        User user = userService.add(userDto);
        User anotherUser = userService.add(anotherUserDto);

        assertEquals(user, userService.getById(user.getId()));
        assertEquals(anotherUser, userService.getById(anotherUser.getId()));
    }

}
