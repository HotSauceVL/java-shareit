package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.StateValidationException;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GateWayBookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId,
			@RequestBody @Valid BookingInputDto bookingInputDto) {
		log.info("Creating booking {}, userId={}", bookingInputDto, userId);
		return bookingClient.bookItem(userId, bookingInputDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking by id {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> bookingConfirmation(@RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId,
												@PathVariable @PositiveOrZero long bookingId,
												@RequestParam(value = "approved") boolean approved) {
		log.info("Patch confirm booking {}, userId={}", bookingId, userId);
		return bookingClient.bookingConfirmation(userId, bookingId, approved);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId,
								@RequestParam(name = "state", defaultValue = "all") String stateParam,
								@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
								@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new StateValidationException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") @PositiveOrZero long userId,
								@RequestParam(name = "state", defaultValue = "all") String stateParam,
								@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
								@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new StateValidationException("Unknown state: " + stateParam));
		log.info("Get booking with state by owner {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getAllBookingByOwner(userId, state, from, size);
	}
}
