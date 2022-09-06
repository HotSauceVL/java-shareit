package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.StateStatus;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;

import javax.xml.bind.ValidationException;
import java.util.List;

public interface BookingService {
    Booking add(Long userId, BookingInputDto bookingInputDto) throws ValidationException;

    Booking bookingConfirmation(Long userId, Long bookingId, boolean approved);

    Booking getById(Long userId, Long bookingId);

    List<Booking> getAllBookingByUser(Long userId, StateStatus state);

    List<Booking> getAllBookingByOwner(Long userId, StateStatus state);
}
