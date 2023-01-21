package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.PageCreator;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceForItem;
import ru.practicum.shareit.error.exception.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.comment.CommentsRepository;
import ru.practicum.shareit.item.dao.repository.ItemRepository;
import ru.practicum.shareit.requests.service.ItemRequestCreator;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingServiceForItem bookingService;
    private final CommentsRepository commentsRepository;
    private final ItemRequestCreator itemRequestCreator;

    @Override
    public Item getById(Long itemId, Long userId) {
        Item item = getItem(itemId);
        itemSetCommentsAndBookings(item);
        if (item.getOwner().getId() != userId) {
            item.setLastBooking(null);
            item.setNextBooking(null);
        }
        return item;
    }

    @Override
    public List<Item> getAllByUserId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(PageCreator.getPage(from, size), size);
        List<Item> items = itemRepository.findByOwner(userService.getById(userId), pageable);
        items.forEach(this::itemSetCommentsAndBookings);
        return items.stream().sorted(Comparator.comparing(Item::getId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchByText(String text, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(PageCreator.getPage(from, size), size);
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.searchByText(text, pageable).stream().filter(Item::isAvailable)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Item add(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userService.getById(userId));
        if (itemDto.getRequestId() != null) {
            item.setItemRequest(itemRequestCreator.getById(itemDto.getRequestId()));
        }
        return itemRepository.save(item);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Item update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = getItem(itemId);
        checkOwner(userId, item);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            item.setItemRequest(itemRequestCreator.getById(itemDto.getRequestId()));
        }
        return itemRepository.save(item);
    }

    @Override
    public void delete(Long userId, Long itemId) {
        checkOwner(userId, getItem(itemId));
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Comment addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (bookingService.checkBooking(userId, itemId, BookingStatus.APPROVED)) {
            return  commentsRepository.save(CommentMapper.toComment(commentDto, userService.getById(userId),
                    getItem(itemId)));
        } else {
          throw new BookingBadRequestException("Пользователь " + userId + " не брал вещь " + itemId + " в аренду");
        }
    }

    @Override
    public Set<Item> getAllByRequestId(Long requestId) {
        return itemRepository.findAllByItemRequest_Id(requestId);
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмета с таким id: " + itemId + " не существует"));
    }

    private void checkOwner(Long userId, Item item) {
        if (item.getOwner().getId() != userId) {
            throw new AccessErrorException("Пользователь не является владельцем вещи");
        }
    }

    private void itemSetCommentsAndBookings(Item item) {
        item.setComments(commentsRepository.findAllByItem_Id(item.getId()).orElse(new ArrayList<>()));
        item.setLastBooking(bookingService.getLastBooking(item.getId()).orElse(null));
        item.setNextBooking(bookingService.getNextBooking(item.getId()).orElse(null));
    }
}
