package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequest> getAllItemRequestsByUserId(long userId);

    ItemRequest add(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequest> getAllItemRequestsCreatedAnotherUsers(long userId, Integer from, Integer size);

    ItemRequest getItemRequestById(long requestId, long userId);
}
