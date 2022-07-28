package ru.practicum.shareit.requests.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private long id;
    private String description;
    private User requestor;
    private LocalDateTime createdTime;
}
