package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getComments() != null && item.getComments().size() != 0 ? item.getComments().stream()
                        .map(CommentMapper::toCommentDto).collect(Collectors.toList()) : new ArrayList<>(),
                item.getItemRequest() != null ? item.getItemRequest().getId() : null
        );
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(Item item) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getComments() != null && item.getComments().size() != 0 ? item.getComments().stream()
                        .map(CommentMapper::toCommentDto).collect(Collectors.toList()) : new ArrayList<>(),
                item.getLastBooking() != null ? BookingMapper.toBookingDtoForItem(item.getLastBooking()) : null,
                item.getNextBooking() != null ? BookingMapper.toBookingDtoForItem(item.getNextBooking()) : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                null,
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

}
