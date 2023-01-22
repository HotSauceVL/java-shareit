package ru.practicum.shareit;

import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.ConflictException;

public class PageCreator {

    public static Integer getPage(Integer from, Integer size) {
        if (from < 0) {
            throw new ConflictException("Стартовый элемент не может быть меньше 0");
        }
        if (size == 0) {
            throw new ConflictException("Размер не может быть равен 0");
        }
        return from > size ? 0 : from / size;
    }
}
