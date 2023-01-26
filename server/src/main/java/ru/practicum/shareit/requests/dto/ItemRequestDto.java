package ru.practicum.shareit.requests.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private Set<ItemDto> items;
}
