package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.exception.BookingBadRequestException;
import ru.practicum.shareit.error.exception.ItemNotFoundException;
import ru.practicum.shareit.error.exception.UserNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dao.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    private final String baseUrl = "/items";

    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;

    private final MockMvc mockMvc;

    private final ObjectMapper mapper;

    private final User user = User.builder()
            .id(1L)
            .name("User")
            .email("user@yandex.ru").build();

    private final Item item = Item.builder()
            .id(1L)
            .name("Item for test")
            .description("Item description")
            .available(true)
            .owner(user)
            .bookings(new ArrayList<>())
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(item.getId())
            .name("Item for test")
            .description("Item description")
            .available(true)
            .build();

    CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .item(item.getId())
            .authorName(user.getName())
            .text("Комментарий")
            .created(LocalDateTime.now())
            .build();
    Comment comment = Comment.builder()
            .id(1L)
            .item(item)
            .author(user)
            .text("Комментарий")
            .created(LocalDateTime.now())
            .build();


    @Test
    void getByIdShouldReturn404WhereWrongItemId() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenThrow(new ItemNotFoundException(""));

        mockMvc.perform(get(baseUrl + "/{id}", 99L).header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByIdShouldReturnItem() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(item);

        mockMvc.perform(get(baseUrl + "/{id}", item.getId()).header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(item.getName())));
    }

    @Test
    void getAllByUserIdShouldReturnEmptyListWhenUserDontHaveItem() throws Exception {
        when(userService.getById(anyLong())).thenReturn(user);
        when(itemRepository.findByOwner(any(), any())).thenReturn((new ArrayList<>()));
        mockMvc.perform(get(baseUrl).header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void getAllByUserIdShouldReturnListOfItem() throws Exception {
        when(itemService.getAllByUserId(anyLong(), any(), any())).thenReturn(List.of(item));

        mockMvc.perform(get(baseUrl).header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].name", is(item.getName())));
    }

    @Test
    void getAllByUserIdShouldAnswer404WithWrongUserId() throws Exception {
        when(itemService.getAllByUserId(anyLong(), any(), any())).thenThrow(new UserNotFoundException(""));

        mockMvc.perform(get(baseUrl).header("X-Sharer-User-Id", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void addShouldAnswer404WhenUserIdIsWrong() throws Exception {
        when(itemService.add(anyLong(), any())).thenThrow(new UserNotFoundException(""));

        mockMvc.perform(post(baseUrl)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void addShouldReturnItem() throws Exception {
        when(itemService.add(anyLong(), any())).thenReturn(item);

        mockMvc.perform(post(baseUrl)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    void updateShouldReturnUpdatedItem() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(item);

        mockMvc.perform(patch(baseUrl + "/{id}", item.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }


    @Test
    void searchByTextShouldReturnItems() throws Exception {
        when(itemService.searchByText(any(), any(), any())).thenReturn(List.of(item));

        mockMvc.perform(get(baseUrl + "/search").param("text", item.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void addCommentShouldAnswer400WhereBookingIsWrong() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenThrow(new BookingBadRequestException(""));

        mockMvc.perform(post(baseUrl + "/{itemId}/comment", 99L)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addCommentShouldReturnItem() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(comment);

        mockMvc.perform(post(baseUrl + "/{itemId}/comment", item.getId())
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

}
