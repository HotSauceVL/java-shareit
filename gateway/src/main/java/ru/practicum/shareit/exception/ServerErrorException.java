package ru.practicum.shareit.exception;

public class ServerErrorException extends Throwable {
    public ServerErrorException(String message) {
        super(message);
    }
}
