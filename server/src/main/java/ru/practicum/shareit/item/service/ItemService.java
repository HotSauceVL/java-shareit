package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemService {
    Item getById(Long itemId, Long userId);

    List<Item> getAllByUserId(Long userId, Integer from, Integer size);

    List<Item> searchByText(String text, Integer from, Integer size);

    Item add(Long userId, ItemDto itemDto);

    Item update(Long userId, Long itemId, ItemDto itemDto);

    void delete(Long userId, Long itemId);

    Comment addComment(Long userId, Long itemId, CommentDto commentDto);

    Set<Item> getAllByRequestId(Long requestId);
}
