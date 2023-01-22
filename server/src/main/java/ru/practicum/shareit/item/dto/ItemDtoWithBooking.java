package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingOutputDtoForItem;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemDtoWithBooking {
        private long id;
        private String name;
        private String description;
        private Boolean available;
        private List<CommentDto> comments;
        private BookingOutputDtoForItem lastBooking;
        private BookingOutputDtoForItem nextBooking;
}
