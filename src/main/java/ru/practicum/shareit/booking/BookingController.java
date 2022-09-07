package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingOutputDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                               @Valid @RequestBody BookingInputDto bookingInputDto, HttpServletRequest httpServletRequest)
            throws ValidationException {
        log.info("Получен запрос к эндпоинту: {} {}, userId {}, тело запроса {}",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), userId, bookingInputDto);
        return BookingMapper.toBookingDto(bookingService.add(userId, bookingInputDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto bookingConfirmation(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam(value = "approved", required = true) boolean approved,
                                                HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, userId {}, bookingId {}, статус {}",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), userId, bookingId, approved);
        return BookingMapper.toBookingDto(bookingService.bookingConfirmation(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable Long bookingId, HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, userId {}, bookingId {}",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), userId, bookingId);
        return BookingMapper.toBookingDto(bookingService.getById(userId, bookingId));
    }

    @GetMapping
    public List<BookingOutputDto> getAllBookingByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(value = "state", required = false,
                                                        defaultValue = "ALL") StateStatus state,
                                                     HttpServletRequest httpServletRequest) {

        log.info("Получен запрос к эндпоинту: {} {}, значение X-Sharer-User-Id {}, параметр state {}, тело запроса {}",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), userId, state);
        return bookingService.getAllBookingByUser(userId, state)
                .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(value = "state", required = false,
                                                        defaultValue = "ALL") StateStatus state,
                                                      HttpServletRequest httpServletRequest) {
        log.info("Получен запрос к эндпоинту: {} {}, значение X-Sharer-User-Id {}, параметр state {}, тело запроса {}",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), userId, state);
        return bookingService.getAllBookingByOwner(userId, state)
                .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
