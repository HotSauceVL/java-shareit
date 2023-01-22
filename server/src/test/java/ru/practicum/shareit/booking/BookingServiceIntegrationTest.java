package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

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

    private final BookingInputDto bookingCurrentDto = BookingInputDto.builder()
            .startDate(LocalDateTime.now().minusDays(1))
            .endDate(LocalDateTime.now().plusDays(1))
            .status(BookingStatus.APPROVED)
            .build();

    private final BookingInputDto bookingFutureDto = BookingInputDto.builder()
            .startDate(LocalDateTime.now().plusDays(2))
            .endDate(LocalDateTime.now().plusDays(3))
            .status(BookingStatus.APPROVED)
            .build();

    @BeforeEach
    void dropTabs() {

    }

    @Test
    void getAllBookingByOwnerShouldReturnListOfBookings() throws ValidationException {
        User ownerUser = userService.add(ownerUserDto);
        User bookerUser = userService.add(bookerUserDto);
        Item item = itemService.add(ownerUser.getId(), itemDto);
        bookingCurrentDto.setItemId(item.getId());
        bookingCurrentDto.setBookerId(bookerUser.getId());
        bookingFutureDto.setItemId(item.getId());
        bookingFutureDto.setBookerId(bookerUser.getId());
        Booking  bookingCurrent = bookingService.add(bookerUser.getId(), bookingCurrentDto);
        Booking  bookingFuture = bookingService.add(bookerUser.getId(), bookingFutureDto);

        assertEquals(List.of(bookingCurrent), bookingService
                .getAllBookingByOwner(ownerUser.getId(), StateStatus.CURRENT, 0, 2));

        assertEquals(List.of(bookingFuture), bookingService
                .getAllBookingByOwner(ownerUser.getId(), StateStatus.FUTURE, 0, 2));
    }

}
