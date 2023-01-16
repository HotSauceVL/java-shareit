package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.PageCreator;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.StateStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dao.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.xml.bind.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking add(Long userId, BookingInputDto bookingInputDto) throws ValidationException {
        Booking booking = BookingMapper.toBooking(bookingInputDto, userService.getById(userId),
                itemService.getById(bookingInputDto.getItemId(), userId));
        checkInputBookingDto(userId, booking);
        booking.setStatus(BookingStatus.WAITING);
        checkItemAvailable(itemService.getById(bookingInputDto.getItemId(), userId));
        return bookingRepository.save(booking);
    }

    @Override
    public Booking bookingConfirmation(Long userId, Long bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BookingConfirmationException("Нельзя изменить статус одобренного бронирования");
        }
        BookingStatus status;
        if (approved) {
            status = BookingStatus.APPROVED;
        } else {
            status = BookingStatus.REJECTED;
        }
        if (checkOwner(userId, booking)) {
            booking.setStatus(status);
        } else {
            throw new AccessErrorException("Пользователь не является владельцем вещи");
        }

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getById(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (checkOwner(userId, booking) || booking.getBooker().getId() == userId) {
            return booking;
        } else {
            throw new AccessErrorException("Ошибка доступа, пользователь не является" +
                    " владельцем или арендатором вещи");
        }
    }

    @Override
    public List<Booking> getAllBookingByUser(Long userId, StateStatus state, Integer from, Integer size) {
        userService.getById(userId);
        Pageable pageable = PageRequest.of(PageCreator.getPage(from, size), size,
                Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings = bookingRepository.findAllByBooker_Id(userId, pageable)
                .orElseThrow(() -> new BookingNotFoundException("Бронирований для пользователя " + userId
                        + " не найдено"));
        return getBookingByStateStatus(bookings, state);
    }

    @Override
    public List<Booking> getAllBookingByOwner(Long userId, StateStatus state, Integer from, Integer size) {
        userService.getById(userId);
        Pageable pageable = PageRequest.of(PageCreator.getPage(from, size), size,
                Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(userId, pageable)
                .orElseThrow(() -> new BookingNotFoundException("Бронирований вещей пользователя " + userId
                        + " не найдено"));
        return getBookingByStateStatus(bookings, state);
    }

   private List<Booking> getBookingByStateStatus(List<Booking> bookings, StateStatus state) {
       switch (state) {
           case ALL:
               return bookings;
           case CURRENT:
               return bookings.stream().filter(booking -> LocalDateTime.now().isAfter(booking.getStart()) &&
                       LocalDateTime.now().isBefore(booking.getEnd())).collect(Collectors.toList());
           case PAST:
               return bookings.stream().filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                       .collect(Collectors.toList());
           case FUTURE:
               return bookings.stream().filter(booking -> LocalDateTime.now().isBefore(booking.getStart()))
                       .collect(Collectors.toList());
           case WAITING:
               return bookings.stream().filter(booking -> booking.getStatus() == BookingStatus.WAITING)
                       .collect(Collectors.toList());
           case REJECTED:
               return bookings.stream().filter(booking -> booking.getStatus() == BookingStatus.REJECTED)
                       .collect(Collectors.toList());
           default:
               throw new BookingBadRequestException("Использование такого статуса невозможно");
       }
   }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
               .orElseThrow(() -> new BookingNotFoundException("Бронирования с таким id: "
                       + bookingId + " не существует"));
    }

    private boolean checkOwner(Long userId, Booking booking) {
       return booking.getItem().getOwner().getId() == userId;
    }

    private void checkItemAvailable(Item item) {
        if (!item.isAvailable()) {
            throw new BadRequestException("Вещь " + item.getId() + " недоступна");
        }
    }

    private void checkInputBookingDto(long userId, Booking booking)
            throws BadRequestException, AccessErrorException {
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new BadRequestException("Время начала бронирования не может быть позже времени" +
                    " окончания бронирования");
        }
        if (booking.getStart() == booking.getEnd()) {
            throw new BadRequestException("Время начала бронирования не может быть равно времени" +
                    " окончания бронирования");
        }
        if (userId == booking.getItem().getOwner().getId()) {
            throw new AccessErrorException("Пользователь " + userId + " не может забронировать свою вещь "
                    + booking.getItem().getId());
        }
    }

}
