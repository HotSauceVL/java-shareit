package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingServiceForItem bookingService;
    private final CommentsRepository commentsRepository;

    @Override
    public Item getById(long itemId, long userId) {
        Item item = getItem(itemId);
        itemSetCommentsAndBookings(item);
        if (item.getOwner().getId() != userId) {
            item.setLastBooking(null);
            item.setNextBooking(null);
        }
        return item;
    }

    @Override
    public List<Item> getAllByUserId(long userId) {
        List<Item> items = new ArrayList<>(itemRepository.findByOwner(userService.getById(userId)));
        items.forEach(this::itemSetCommentsAndBookings);
        return items.stream().sorted(Comparator.comparing(Item::getId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.searchByText(text).stream().filter(Item::isAvailable).collect(Collectors.toList());
        }
    }

    @Override
    public Item add(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userService.getById(userId));
        return itemRepository.save(item);
    }

    @Override
    public Item update(long userId, long itemId, ItemDto itemDto) {
        Item item = getItem(itemId);
        checkOwner(userId, itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemRepository.save(item);
    }

    @Override
    public void delete(long userId, long itemId) {
        checkOwner(userId, itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public Comment addComment(long userId, long itemId, CommentDto commentDto) {
        if (bookingService.checkBooking(userId, itemId, BookingStatus.APPROVED)) {
            return  commentsRepository.save(CommentMapper.toComment(commentDto, userService.getById(userId),
                    getItem(itemId)));
        } else {
          throw new BookingBadRequestException("Пользователь " + userId + " не брал вещь " + itemId + " в аренду");
        }
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмета с таким id: " + itemId + " не существует"));
    }

    private void checkOwner(Long userId, Long itemId) {
        Item item = getItem(itemId);
        if (item.getOwner().getId() != userId) {
            throw new AccessErrorException("Пользователь не является владельцем вещи");
        }
    }

    private void itemSetCommentsAndBookings(Item item) {
        item.setComments(new ArrayList<>(commentsRepository.findAllByItem_Id(item.getId())));
        item.setLastBooking(bookingService.getLastBooking(item.getId()).orElse(null));
        item.setNextBooking(bookingService.getNextBooking(item.getId()).orElse(null));
    }
}
