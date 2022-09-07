package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.StateStatus;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dao.repository.BookingRepository;
import ru.practicum.shareit.error.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking add(Long userId, BookingInputDto bookingInputDto) throws ValidationException {
        checkInputBookingDto(userId, bookingInputDto);
        bookingInputDto.setStatus(BookingStatus.WAITING);
        checkItemAvailable(itemService.getById(bookingInputDto.getItemId(), userId));
        return bookingRepository.save(BookingMapper.toBooking(bookingInputDto, userService.getById(userId),
                itemService.getById(bookingInputDto.getItemId(), userId)));
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
    public List<Booking> getAllBookingByUser(Long userId, StateStatus state) {
        userService.getById(userId);
        switch (state) {
            case CURRENT:
                return new ArrayList<>(bookingRepository
                        .findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(),
                                LocalDateTime.now())
                        .orElseThrow(() -> new BookingNotFoundException("Текущих бронирований для пользователя "
                                + userId + " не найдено")));
            case PAST:
                return new ArrayList<>(bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId,
                                LocalDateTime.now())
                        .orElseThrow(() -> new BookingNotFoundException("Завершенных бронирований для пользователя "
                                + userId + " не найдено")));
            case FUTURE:
                return new ArrayList<>(bookingRepository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now())
                        .orElseThrow(() -> new BookingNotFoundException("Будущих бронирований для пользователя "
                                + userId + " не найдено")));
            case WAITING:
                return new ArrayList<>(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId,
                                BookingStatus.WAITING)
                        .orElseThrow(() -> new BookingNotFoundException("Ожидающих подтверждения бронирований " +
                                "для пользователя " + userId + " не найдено")));
            case REJECTED:
                return new ArrayList<>(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId,
                                BookingStatus.REJECTED)
                        .orElseThrow(() -> new BookingNotFoundException("Отклоненных бронирований для пользователя "
                                + userId + " не найдено")));
            default:
                return new ArrayList<>(bookingRepository.findAllByBooker_IdOrderByStartDesc(userId)
                        .orElseThrow(() -> new BookingNotFoundException("Бронирований для пользователя "
                                + userId + " не найдено")));
        }
    }

    @Override
    public List<Booking> getAllBookingByOwner(Long userId, StateStatus state) {
        List<Item> items = itemService.getAllByUserId(userId);
        if (items.size() == 0) {
            throw new ItemNotFoundException("У данного пользователя " + userId + " нет вещей");
        }
        List<Booking> bookings =  new ArrayList<>();
        switch (state) {
            case CURRENT:
                items.forEach(item -> bookings.addAll(bookingRepository
                        .findAllByItem_IdAndStartBeforeAndEndAfterOrderByStartDesc(item.getId(), LocalDateTime.now(),
                                LocalDateTime.now())));
                if (bookings.size() != 0) {
                   return bookings;
                } else {
                    throw new BookingNotFoundException("Текущих бронирований для вещей пользователя "
                            + userId + " не найдено");
                }
            case PAST:
                items.forEach(item -> bookings.addAll(bookingRepository
                        .findAllByItem_IdAndEndIsBeforeOrderByStartDesc(item.getId(), LocalDateTime.now())));
                if (bookings.size() != 0) {
                    return bookings;
                } else {
                    throw new BookingNotFoundException("Прошедших бронирований для вещей пользователя "
                            + userId + " не найдено");
                }
            case FUTURE:
                items.forEach(item -> bookings.addAll(bookingRepository
                        .findAllByItem_IdAndStartIsAfterOrderByStartDesc(item.getId(), LocalDateTime.now())));
                if (bookings.size() != 0) {
                    return bookings;
                } else {
                    throw new BookingNotFoundException("Прошедших бронирований для вещей пользователя "
                            + userId + " не найдено");
                }
            case WAITING:
                items.forEach(item -> bookings.addAll(bookingRepository
                        .findAllByItem_IdAndStatusOrderByStartDesc(item.getId(), BookingStatus.WAITING)));
                if (bookings.size() != 0) {
                    return bookings;
                } else {
                    throw new BookingNotFoundException("Прошедших бронирований для вещей пользователя "
                            + userId + " не найдено");
                }
            case REJECTED:
                items.forEach(item -> bookings.addAll(bookingRepository
                        .findAllByItem_IdAndStatusOrderByStartDesc(item.getId(), BookingStatus.REJECTED)));
                if (bookings.size() != 0) {
                    return bookings;
                } else {
                    throw new BookingNotFoundException("Прошедших бронирований для вещей пользователя "
                            + userId + " не найдено");
                }
            default:
                items.forEach(item -> bookings.addAll(bookingRepository
                        .findAllByItem_IdOrderByStartDesc(item.getId())));
                if (bookings.size() != 0) {
                    return bookings;
                } else {
                    throw new BookingNotFoundException("Прошедших бронирований для вещей пользователя "
                            + userId + " не найдено");
                }
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
            throw new ItemAvailableException("Вещь " + item.getId() + " недоступна");
        }
    }

    private void checkInputBookingDto(long userId, BookingInputDto bookingInputDto) throws ValidationException {
        if (bookingInputDto.getStartDate().isAfter(bookingInputDto.getEndDate())) {
            throw new ValidationException("Время начала бронирования не может быть позже времени" +
                    " окончания бронирования");
        }
        if (bookingInputDto.getStartDate() == bookingInputDto.getEndDate()) {
            throw new ValidationException("Время начала бронирования не может быть равно времени" +
                    " окончания бронирования");
        }
        if (userId == itemService.getById(bookingInputDto.getItemId(), userId).getOwner().getId()) {
            throw new AccessErrorException("Пользователь " + userId + " не может забронировать свою вещь "
                    + bookingInputDto.getItemId());
        }
    }
}
