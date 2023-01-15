package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {

    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final UserDto userDto = UserDto.builder()
            .name("User")
            .email("User@yandex.ru").build();

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .description("Request description")
            .build();

    private final ItemRequestDto anotherItemRequestDto = ItemRequestDto.builder()
            .description("Another Request description")
            .build();

    @Test
    void getAllItemRequestsByUserIdShouldReturnListOfItem() {
        User user = userService.add(userDto);
        ItemRequest itemRequest = itemRequestService.add(user.getId(), itemRequestDto);
        ItemRequest anotherItemRequest = itemRequestService.add(user.getId(), anotherItemRequestDto);

        assertEquals(itemRequest.getId(), itemRequestService
                .getAllItemRequestsByUserId(user.getId()).get(0).getId());
        assertEquals(anotherItemRequest.getId(), itemRequestService
                .getAllItemRequestsByUserId(user.getId()).get(1).getId());
    }

}
