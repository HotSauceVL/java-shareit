package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerIntegrationTest {

    private final ItemController itemController;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private final UserDto ownerUserDto = UserDto.builder()
            .name("Owner")
            .email("owner@yandex.ru").build();

    private final UserDto bookerUserDto = UserDto.builder()
            .name("Booker")
            .email("booker@yandex.ru").build();

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

    private final BookingInputDto bookingCurrentDto = BookingInputDto.builder()
            .startDate(LocalDateTime.now().minusDays(2))
            .endDate(LocalDateTime.now().minusDays(1))
            .status(BookingStatus.APPROVED)
            .build();

    private final BookingInputDto bookingFutureDto = BookingInputDto.builder()
            .startDate(LocalDateTime.now().plusDays(2))
            .endDate(LocalDateTime.now().plusDays(3))
            .status(BookingStatus.APPROVED)
            .build();

    @Test
    void getAllByUserIdShouldReturnListOfItem() throws Exception {
        User ownerUser = userService.add(ownerUserDto);
        User bookerUser = userService.add(bookerUserDto);
        Item item = itemService.add(ownerUser.getId(), itemDto);
        Item anotherItem = itemService.add(ownerUser.getId(), anotherItemDto);
        bookingCurrentDto.setItemId(item.getId());
        bookingCurrentDto.setBookerId(bookerUser.getId());
        bookingFutureDto.setItemId(item.getId());
        bookingFutureDto.setBookerId(bookerUser.getId());
        Booking bookingPast = bookingService.add(bookerUser.getId(), bookingCurrentDto);
        Booking  bookingFuture = bookingService.add(bookerUser.getId(), bookingFutureDto);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        assertEquals(item.getId(), itemController.getAllByUserId(ownerUser.getId(), 0, 2, httpServletRequest).get(0).getId());
        assertEquals(bookingPast.getId(), itemController.getAllByUserId(ownerUser.getId(), 0, 2, httpServletRequest).get(0).getLastBooking().getId());
        assertEquals(bookingFuture.getId(), itemController.getAllByUserId(ownerUser.getId(), 0, 2, httpServletRequest).get(0).getNextBooking().getId());
    }

}
