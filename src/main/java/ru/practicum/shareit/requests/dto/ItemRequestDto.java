package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    @NotBlank
    private String description;
    private LocalDateTime created;
    private Set<ItemDto> items;
}
