package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    private final String baseUrl = "/users";
    @MockBean
    private UserServiceImpl userService;
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    private final User user = User.builder()
            .id(1L)
            .name("User")
            .email("user@yandex.ru").build();

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("User")
            .email("user@yandex.ru").build();

    @Test
    void getAllShouldReturnUsersList() throws Exception {
        when(userService.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void getAllShouldReturnEmptyListWhenThereIsNoUser() throws Exception {
        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void getByIdShouldAnswer404WhenUserNotFound() throws Exception {
        when(userService.getById(anyLong())).thenThrow(new UserNotFoundException(""));

        mockMvc.perform(get(baseUrl + "/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByIdShouldReturnUser() throws Exception {
        when(userService.getById(anyLong())).thenReturn(user);

        mockMvc.perform(get(baseUrl + "/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("User")));
    }

    @Test
    void addUserShouldReturnUser() throws Exception {
        when(userService.add(any())).thenReturn(user);

        mockMvc.perform(post(baseUrl)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @Test
    void updateUserShouldReturnUpdatedUser() throws Exception {
        when(userService.update(anyLong(), any())).thenReturn(user);

        mockMvc.perform(patch(baseUrl + "/{id}", user.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("User")));
    }

    @Test
    public void whenDeleteUser_thenReturnStatusOK() throws Exception {
        mockMvc.perform(delete(baseUrl + "/{id}", user.getId()))
                .andExpect(status().isOk());
    }

}
