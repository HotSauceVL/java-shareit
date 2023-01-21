package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {

    private final BookingRepository bookingRepository;

    private final TestEntityManager testEntityManager;

    private final User ownerUser = User.builder()
            .name("Owner")
            .email("owner@yandex.ru").build();
    private final User bookerUser = User.builder()
            .name("Booker")
            .email("booker@yandex.ru").build();

    private final Item item = Item.builder()
            .name("Item")
            .description("Item description")
            .available(true)
            .owner(ownerUser)
            .bookings(new ArrayList<>())
            .build();

    private final Booking booking = Booking.builder()
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now().plusDays(1))
            .item(item)
            .booker(bookerUser)
            .status(BookingStatus.APPROVED)
            .build();

    @Test
    void getAllBookingByUserShouldReturnListOfBookings() {
        testEntityManager.getEntityManager().persist(bookerUser);
        testEntityManager.getEntityManager().persist(ownerUser);
        testEntityManager.getEntityManager().persist(item);
        testEntityManager.getEntityManager().persist(booking);

        List<Booking> bookings = bookingRepository.findAllByBooker_Id(bookerUser.getId(),
                        PageRequest.of(0, 2)).orElse(new ArrayList<>());
        assertEquals(bookings.size(), 1);
    }

}
