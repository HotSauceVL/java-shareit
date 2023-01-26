package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GateWayRequestController {
    private final RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id")
                                                                 @PositiveOrZero long userId) {
        log.info("Get all requests by user id {}", userId);
        return requestClient.getAllItemRequestsByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Add request {}, user {}", itemRequestDto, userId);
        return requestClient.add(userId, itemRequestDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsCreatedAnotherUsers(
            @RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", required = false, defaultValue = "20") @Positive int size) {
        log.info("Get all requests created by another user {}, from {}, size {}", userId, from, size);
        return requestClient.getAllItemRequestsCreatedAnotherUsers(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable @PositiveOrZero long requestId,
                                                     @RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId) {
        log.info("Get request by id {}, user {}", requestId, userId);
        return requestClient.getItemRequestById(requestId, userId);
    }

}
