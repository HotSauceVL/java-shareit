package ru.practicum.shareit.booking.dao.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<List<Booking>> findAllByBooker_Id(Long bookerId, Pageable pageable);

    Optional<List<Booking>> findAllByItemOwnerId(Long ownerId, Pageable pageable);

    boolean existsBookingByBooker_IdAndItem_IdAndStatusEqualsAndEndIsBefore(long bookerId, long itemId,
                                                                            BookingStatus status, LocalDateTime end);

    Optional<Booking> findFirstBookingByItem_IdAndEndIsBeforeOrderByEndDesc(long itemId, LocalDateTime now);

    Optional<Booking> findFirstBookingByItem_IdAndStartIsAfterOrderByStart(long itemId, LocalDateTime now);

}
