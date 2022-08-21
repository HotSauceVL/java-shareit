package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId).get());
    }

    @Override
    public List<ItemDto> getAllByUserId(long userId) {
        return
                itemDao.getAllByUserId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemDao.searchByText(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        }
    }

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.findById(userId).get());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);


        return ItemMapper.toItemDto(
                itemDao.update(userDao.getById(userId), itemId, itemDto));
    }

    @Override
    public void delete(long userId, long itemId) {
        itemDao.delete(userId, itemId);
    }
}
