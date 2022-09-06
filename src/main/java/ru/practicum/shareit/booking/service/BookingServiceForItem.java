package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

public interface BookingServiceForItem {
    Optional<Booking> getLastBooking(long itemId);

    Optional<Booking> getNextBooking(long itemId);

    boolean checkBooking(long userId, long itemId, BookingStatus status);
}
