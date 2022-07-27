package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class Item {
    private long id;
    private User owner;
    private String name;
    private String description;
    private boolean available;
    private ItemRequest request;
}
