package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.PageCreator;
import ru.practicum.shareit.error.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.dao.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    private final ItemService itemService;

    @Override
    public List<ItemRequest> getAllItemRequestsByUserId(long userId) {
        userService.getById(userId);
        return itemRequestRepository.findAllByRequestor_Id(userId)
                .orElse(new ArrayList<>()).stream().map(this::setRequestItems).collect(Collectors.toList());
    }

    @Override
    public ItemRequest add(long userId, ItemRequestDto itemRequestDto) {
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userService.getById(userId));
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getAllItemRequestsCreatedAnotherUsers(long userId, Integer from, Integer size) {
        userService.getById(userId);
        Pageable pageable = PageRequest.of(PageCreator.getPage(from, size), size,
                Sort.by(Sort.Direction.DESC, "createdTime"));
        return itemRequestRepository.findAllByRequestor_IdIsNot(userId, pageable)
                .orElse(itemRequestRepository.findAll()).stream().map(this::setRequestItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequest getItemRequestById(long requestId, long userId) {
        userService.getById(userId);
        return setRequestItems(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запроса " +
                "вещи с таким id: " + requestId + " не существует")));
    }

    private ItemRequest setRequestItems(ItemRequest request) {
        request.setItems(itemService.getAllByRequestId(request.getId()));
        return request;
    }
}
