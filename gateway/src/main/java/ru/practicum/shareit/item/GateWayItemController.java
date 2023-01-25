package ru.practicum.shareit.item;

import jdk.jshell.Snippet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GateWayItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid ItemDto item,
                                      @RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId) {
        log.info("Add item {}, user {}", item, userId);
        return itemClient.add(userId, item);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDto item,
                                         @RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId,
                                         @PathVariable @PositiveOrZero long itemId) {
        log.info("Update item {}, user {}", itemId, userId);
        return itemClient.update(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId,
                                          @PathVariable @PositiveOrZero long itemId) {
        log.info("Get item by id {}, user {}", itemId, userId);
        return itemClient.getById(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllbyUserId(@RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId,
                                         @PositiveOrZero @RequestParam(name = "from", required = false,
                                                 defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", required = false,
                                                 defaultValue = "20") Integer size) {
        log.info("Get all items by user {}, from {}, size {}", userId, from, size);
        return itemClient.getAllByUserId(userId, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Delete item {}, user {}", itemId, userId);
        return itemClient.delete(userId, itemId);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto comment,
                                                @RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId,
                                                @PathVariable @PositiveOrZero long itemId) {
        log.info("Add comment {}, user {}, item {}", comment, userId, itemId);
        return itemClient.addComment(userId, itemId, comment);
    }

    @GetMapping("search")
    public ResponseEntity<Object> searchByText(@RequestParam String text,
                                         @RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId,
                                         @PositiveOrZero @RequestParam(name = "from", required = false,
                                                 defaultValue = "0") int from,
                                         @Positive @RequestParam(name = "size", required = false,
                                                 defaultValue = "20") int size) {
        log.info("Search items by text {}, user {}, from {}, size {}", text, userId, from, size);
        if (text.isBlank()) return new ResponseEntity<>(new ArrayList<ItemDto>(), HttpStatus.OK) ;
        return itemClient.searchByText(text, userId, from, size);
    }

}
