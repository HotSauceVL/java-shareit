package ru.practicum.shareit.requests.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.HashSet;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreatedTime(),
                itemRequest.getItems() != null
                        ? itemRequest.getItems().stream().map(ItemMapper::toItemDto).collect(Collectors.toSet())
                        : new HashSet<>()
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                null,
                itemRequestDto.getCreated(),
                new HashSet<>()
        );
    }
}
