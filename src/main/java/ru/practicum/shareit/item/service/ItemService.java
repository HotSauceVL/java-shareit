package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getById(long itemId, long userId);

    List<Item> getAllByUserId(long userId);

    List<Item> searchByText(String text);

    Item add(long userId, ItemDto itemDto);

    Item update(long userId, long itemId, ItemDto itemDto);

    void delete(long userId, long itemId);

    Comment addComment(long userId, long itemId, CommentDto commentDto);
}
