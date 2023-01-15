package ru.practicum.shareit;

import ru.practicum.shareit.error.exception.BadRequestException;

public class PageCreator {

    public static Integer getPage(Integer from, Integer size) {
        if (from < 0) {
            throw new BadRequestException("Стартовый элемент не может быть меньше 0");
        }
        return from > size ? 0 : from / size;
    }
}
