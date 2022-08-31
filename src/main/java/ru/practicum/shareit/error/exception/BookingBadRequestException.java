package ru.practicum.shareit.error.exception;

public class BookingBadRequestException extends RuntimeException {
    public BookingBadRequestException(String message) {
        super(message);
    }
}
