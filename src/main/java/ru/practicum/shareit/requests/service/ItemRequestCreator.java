package ru.practicum.shareit.requests.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.requests.dao.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

@Component
@AllArgsConstructor
public class ItemRequestCreator {

    private final ItemRequestRepository itemRequestRepository;

    public ItemRequest getById(long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() -> new ItemRequestNotFoundException("Запроса " +
                "вещи с таким id: " + requestId + " не существует"));
    }
}
