package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingOutputDtoForItem;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemDtoWithBooking {
        @PositiveOrZero
        private long id;
        @NotBlank
        private String name;
        @NotBlank
        private String description;
        @NotNull
        private Boolean available;
        private List<CommentDto> comments;
        private BookingOutputDtoForItem lastBooking;
        private BookingOutputDtoForItem nextBooking;
}
