package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.validate.OnCreate;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getAllItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, значение X-Sharer-User-Id {}", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(), userId);
        return itemRequestService.getAllItemRequestsByUserId(userId)
                .stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());

    }

    @PostMapping
    @Validated(OnCreate.class)
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto,
                              HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, значение X-Sharer-User-Id {}, тело запроса {}",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), userId, itemRequestDto);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.add(userId, itemRequestDto));
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequestsCreatedAnotherUsers(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                      @RequestParam(value = "from", required = false,
                                                                              defaultValue = "0") Integer from,
                                                                      @RequestParam(value = "size", required = false,
                                                                              defaultValue = "20") Integer size,
                                                                      HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, значение X-Sharer-User-Id {}, параметры запроса from {}, size {}",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), userId, from, size);
        return itemRequestService.getAllItemRequestsCreatedAnotherUsers(userId, from, size)
                .stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @GetMapping("{requestId}")
    @Transactional(rollbackOn = Exception.class)
    public ItemRequestDto getItemRequestById(@PathVariable long requestId,
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, параметр пути запроса {}, значение X-Sharer-User-Id {}",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), requestId, userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.getItemRequestById(requestId, userId));
    }

}
