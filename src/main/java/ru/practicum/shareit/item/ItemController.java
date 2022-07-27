package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validate.OnCreate;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam(value = "text", required = false) String text) {
        return itemService.searchByText(text);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

}
