package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.error.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {

    private final String baseUrl = "/requests";

    @MockBean
    private ItemController itemController;
    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private UserService userService;

    private final MockMvc mockMvc;

    private final ObjectMapper mapper;

    private final User user = User.builder()
            .id(1L)
            .name("User")
            .email("user@yandex.ru").build();

    private final User userOwner = User.builder()
            .id(2L)
            .name("UserOwner")
            .email("UserOwner@yandex.ru").build();

    final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("request description")
            .requestor(userOwner)
            .createdTime(LocalDateTime.now())
            .build();
    final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("request description")
            .build();

    @Test
    void addShould404WhenRequestorWrong() throws Exception {
        when(itemRequestService.add(anyLong(), any())).thenThrow(new UserNotFoundException(""));

        mockMvc.perform(post(baseUrl)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void addShouldReturnItemRequest() throws Exception {
        when(itemRequestService.add(anyLong(), any())).thenReturn(itemRequest);

        mockMvc.perform(post(baseUrl)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequestsByUserIdShould404WhenRequestorWrong() throws Exception {
        when(itemRequestService.getAllItemRequestsByUserId(anyLong())).thenThrow(new UserNotFoundException(""));

        mockMvc.perform(get(baseUrl)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemRequestsByUserIdShouldReturnRequest() throws Exception {
        when(itemRequestService.getAllItemRequestsByUserId(anyLong())).thenReturn(List.of(itemRequest));

        mockMvc.perform(get(baseUrl)
                        .header("X-Sharer-User-Id", userOwner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void getItemRequestByIdShould404WhenRequestorNotFound() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenThrow(new UserNotFoundException(""));

        mockMvc.perform(get(baseUrl + "/{requestId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemRequestByIdShould404WhenItemRequestNotFound() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenThrow(new ItemRequestNotFoundException(""));

        mockMvc.perform(get(baseUrl + "/{requestId}", 99L)
                        .header("X-Sharer-User-Id", userOwner.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void  getItemRequestByIdShouldReturnItemRequest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequest);

       mockMvc.perform(get(baseUrl + "/{requestId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", userOwner.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequestsCreatedAnotherUsersShould404WhenRequestorNotFound() throws Exception {
        when(itemRequestService.getAllItemRequestsCreatedAnotherUsers(anyLong(), any(), any()))
                .thenThrow(new UserNotFoundException(""));

        mockMvc.perform(get(baseUrl + "/all").header("X-Sharer-User-Id", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemRequestsCreatedAnotherUsersShouldReturnItemRequests() throws Exception {
        when(itemRequestService.getAllItemRequestsCreatedAnotherUsers(anyLong(), any(), any()))
                .thenReturn(List.of(itemRequest));

        mockMvc.perform(get(baseUrl + "/all")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

}
