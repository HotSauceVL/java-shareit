package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validate.OnCreate;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    @Transactional(rollbackOn = Exception.class)
    public ItemDtoWithBooking getById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                                      HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, параметр пути запроса {}, значение X-Sharer-User-Id {}",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), itemId, userId);
        return ItemMapper.toItemDtoWithBooking(itemService.getById(itemId, userId));
    }

    @GetMapping
    @Transactional(rollbackOn = Exception.class)
    public List<ItemDtoWithBooking> getAllByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(value = "from", required = false,
                                                           defaultValue = "0") Integer from,
                                                   @RequestParam(value = "size", required = false,
                                                           defaultValue = "20") Integer size,
                                                   HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, значение X-Sharer-User-Id {}", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(), userId);
        return itemService.getAllByUserId(userId, from, size)
                .stream().map(ItemMapper::toItemDtoWithBooking).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam(value = "text", required = false) String text,
                                      @RequestParam(value = "from", required = false,
                                              defaultValue = "0") Integer from,
                                      @RequestParam(value = "size", required = false,
                                              defaultValue = "20") Integer size,
                                      HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, значение параметра поиска {}", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(), text);
        return itemService.searchByText(text, from, size).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto,
                       HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, значение X-Sharer-User-Id {}, тело запроса {}",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), userId, itemDto);
        return ItemMapper.toItemDto(itemService.add(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto,
                          HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, значение X-Sharer-User-Id {}, параметр пути {}, тело запроса {}",
                httpServletRequest.getMethod(),httpServletRequest.getRequestURI(), userId, itemId, itemDto);
        return ItemMapper.toItemDto(itemService.update(userId, itemId, itemDto));
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                       HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, значение X-Sharer-User-Id {}, параметр пути {}",
                httpServletRequest.getMethod(),httpServletRequest.getRequestURI(), userId, itemId);
        itemService.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                              @Valid @RequestBody CommentDto commentDto, HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, значение X-Sharer-User-Id {}, параметр пути itemId {}," +
                        " тело запроса {}",
                httpServletRequest.getMethod(),httpServletRequest.getRequestURI(), userId, itemId, commentDto);
        return CommentMapper.toCommentDto(itemService.addComment(userId, itemId, commentDto));
    }
}
