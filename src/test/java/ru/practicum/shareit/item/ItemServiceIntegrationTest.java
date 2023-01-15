package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;

    private final UserDto userDto = UserDto.builder()
            .name("User")
            .email("User@yandex.ru").build();

    private final ItemDto itemDto = ItemDto.builder()
            .name("Item")
            .description("Item description")
            .available(true)
            .build();

    private final ItemDto anotherItemDto = ItemDto.builder()
            .name("Another Item")
            .description("Another Item description")
            .available(true)
            .build();

    @Test
    void getAllByUserIdShouldReturnListOfItem() {
        User user = userService.add(userDto);
        Item item = itemService.add(user.getId(), itemDto);
        Item anotherItem = itemService.add(user.getId(), anotherItemDto);

        assertEquals(List.of(item, anotherItem), itemService.getAllByUserId(user.getId(), 0, 5));
    }

}
