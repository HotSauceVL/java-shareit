package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.error.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemService itemService;
    @Mock
    UserService userService;

    private final LocalDateTime date = LocalDateTime.now();
    private final User ownerUser = User.builder()
            .id(1L)
            .name("Owner")
            .email("owner@yandex.ru").build();
    private final User bookerUser = User.builder()
            .id(2L)
            .name("Booker")
            .email("booker@yandex.ru").build();
    private final Item itemFirst = Item.builder()
            .id(1L)
            .name("FirstItem")
            .description("FirstItemDescription")
            .owner(ownerUser)
            .available(true)
            .build();
    private final BookingInputDto bookingInputDto = BookingInputDto.builder()
            .id(1L)
            .startDate(date.minusDays(1))
            .endDate(date.minusHours(1))
            .itemId(itemFirst.getId())
            .build();
    private final Booking booking = Booking.builder()
            .id(bookingInputDto.getId())
            .start(bookingInputDto.getStartDate())
            .end(bookingInputDto.getEndDate())
            .item(itemFirst)
            .booker(bookerUser)
            .status(BookingStatus.APPROVED)
            .build();
    private final Booking bookingWaitingStatus = Booking.builder()
            .id(2L)
            .start(date.minusDays(1))
            .end(date.minusHours(1))
            .item(itemFirst)
            .booker(bookerUser)
            .status(BookingStatus.WAITING)
            .build();


    @Test
    void addBooking() throws ValidationException {
        when(userService.getById(anyLong())).thenReturn(ownerUser);
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemFirst);
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking bookingTest = bookingService.add(bookerUser.getId(), bookingInputDto);
        assertNotNull(bookingTest);
        assertEquals(booking.getId(), bookingTest.getId());
        assertEquals(booking.getItem().getId(), bookingTest.getItem().getId());
        assertEquals(booking.getStart(), bookingTest.getStart());
        assertEquals(booking.getEnd(), bookingTest.getEnd());

        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void addBookingShouldItemNotFoundExceptionWhenItemWrongId() {
        when(userService.getById(anyLong())).thenReturn(bookerUser);
        when(itemService.getById(anyLong(), anyLong())).thenThrow(new ItemNotFoundException(""));

        assertThrows(ItemNotFoundException.class, () -> bookingService.add(bookerUser.getId(), bookingInputDto));
    }

    @Test
    void addBookingShouldUserNotFoundExceptionWhenBookerWrongId() {
        when(userService.getById(anyLong())).thenThrow(new UserNotFoundException(""));

        assertThrows(UserNotFoundException.class, () -> bookingService.add(99L, bookingInputDto));
    }

    @Test
    void addBookingShouldAccessErrorExceptionWhenBookerIsOwner() {
        when(userService.getById(anyLong())).thenReturn(ownerUser);
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemFirst);

        assertThrows(AccessErrorException.class, () -> bookingService.add(ownerUser.getId(), bookingInputDto));
    }

    @Test
    void bookingConfirmation() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaitingStatus));
        when(bookingRepository.save(any())).thenReturn(bookingWaitingStatus);

        Booking bookingApproved = bookingService.bookingConfirmation(ownerUser.getId(),
                bookingWaitingStatus.getId(), true);
        assertEquals(bookingWaitingStatus.getId(), bookingApproved.getId());
        assertEquals(bookingWaitingStatus.getItem().getId(), bookingApproved.getItem().getId());
        assertEquals(bookingWaitingStatus.getStart(), bookingApproved.getStart());
        assertEquals(bookingWaitingStatus.getEnd(), bookingApproved.getEnd());
        assertEquals(BookingStatus.APPROVED, bookingApproved.getStatus());

        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void bookingConfirmationShouldBookingNotFoundExceptionWhenBookingWrongId() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.bookingConfirmation(ownerUser.getId(),
                        bookingWaitingStatus.getId(), true));
        assertEquals("Бронирования с таким id: "
                + bookingWaitingStatus.getId() + " не существует", exception.getMessage());
    }

    @Test
    void bookingConfirmationShouldAccessErrorExceptionWhenUserIsNotOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaitingStatus));

        assertThrows(AccessErrorException.class, () -> bookingService.bookingConfirmation(bookerUser.getId(),
                        bookingWaitingStatus.getId(), true));
    }

    @Test
    void bookingConfirmationShouldBookingConfirmationExceptionWhenStatusIsApprove() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingConfirmationException exception = assertThrows(BookingConfirmationException.class,
                () -> bookingService.bookingConfirmation(ownerUser.getId(), booking.getId(), true));
        assertEquals("Нельзя изменить статус одобренного бронирования", exception.getMessage());
    }

    @Test
    void getBookingByIdByOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking resultBooking = bookingService.getById(ownerUser.getId(), booking.getId());
        assertNotEquals(resultBooking, null);
        assertEquals(resultBooking.getItem().getId(), itemFirst.getId());
        assertEquals(resultBooking.getBooker().getId(), bookerUser.getId());

        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void getBookingByIdByBooker() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking resultBooking = bookingService.getById(bookerUser.getId(), booking.getId());
        assertNotNull(resultBooking);
        assertEquals(resultBooking.getItem().getId(), itemFirst.getId());
        assertEquals(resultBooking.getBooker().getId(), bookerUser.getId());

        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void getBookingByIdShouldAccessErrorExceptionWhenUserNotOwnerOrBooker() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(AccessErrorException.class, () -> bookingService.getById(99L, booking.getId()));
    }

    @Test
    void getBookingByIdShouldBookingNotFoundExceptionWhenBookingIdWrong() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getById(ownerUser.getId(), 99L));
    }

    @Test
    void getAllBookingByUserShouldReturnListOfBookings() {
        when(bookingRepository.findAllByBooker_Id(anyLong(), any(Pageable.class)))
                .thenReturn(Optional.of(List.of(booking, bookingWaitingStatus)));
        when(userService.getById(anyLong())).thenReturn(bookerUser);

        List<Booking> bookings = bookingService.getAllBookingByUser(bookerUser.getId(), StateStatus.ALL,
                0, 2);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 2);

        verify(bookingRepository, times(1)).findAllByBooker_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllBookingByUserShouldBookingNotFoundExceptionWhenBookingsIsNull() {
        when(bookingRepository.findAllByBooker_Id(anyLong(), any(Pageable.class))).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getAllBookingByUser(bookerUser.getId(), StateStatus.ALL, 0, 2));
    }

    @Test
    void getAllBookingByUserWhenBookingStatusIsWaiting() {
        when(bookingRepository.findAllByBooker_Id(anyLong(), any(Pageable.class)))
                .thenReturn(Optional.of(List.of(booking, bookingWaitingStatus)));

        List<Booking> bookings = bookingService.getAllBookingByUser(bookerUser.getId(), StateStatus.WAITING,
                0, 2);
        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), BookingStatus.WAITING);

        verify(bookingRepository, times(1)).findAllByBooker_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllBookingByUserWhenBookingStatusIsPast() {
        when(bookingRepository.findAllByBooker_Id(anyLong(), any(Pageable.class)))
                .thenReturn(Optional.of(List.of(booking, bookingWaitingStatus)));

        List<Booking> bookings = bookingService.getAllBookingByUser(bookerUser.getId(), StateStatus.PAST,
                0, 2);
        assertNotNull(bookings);
        assertEquals(bookings.size(), 2);

        verify(bookingRepository, times(1)).findAllByBooker_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllBookingByUserWhenBookingStatusIsFuture() {
        final Booking futureBooking = Booking.builder()
                .id(3L)
                .start(date.plusDays(1))
                .end(date.plusDays(2))
                .item(itemFirst)
                .booker(bookerUser)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.findAllByBooker_Id(anyLong(), any(Pageable.class)))
                .thenReturn(Optional.of(List.of(booking, futureBooking)));

        List<Booking> bookings = bookingService.getAllBookingByUser(bookerUser.getId(), StateStatus.FUTURE,
                0, 2);
        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.get(0).getStart().isAfter(date));

        verify(bookingRepository, times(1)).findAllByBooker_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllBookingByBookerWhenBookingStatusIsCurrent() {
        final Booking currentBooking = Booking.builder()
                .id(3L)
                .start(date.minusDays(1))
                .end(date.plusDays(1))
                .item(itemFirst)
                .booker(bookerUser)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.findAllByBooker_Id(anyLong(), any(Pageable.class)))
                .thenReturn(Optional.of(List.of(booking, currentBooking)));

        List<Booking> bookings = bookingService.getAllBookingByUser(bookerUser.getId(), StateStatus.CURRENT,
                0, 2);
        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.get(0).getEnd().isAfter(date));

        verify(bookingRepository, times(1)).findAllByBooker_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllBookingByBookerShouldBookingBadRequestExceptionWhenBookingStatusIsWrong() {
        when(bookingRepository.findAllByBooker_Id(anyLong(), any(Pageable.class)))
                .thenReturn(Optional.of(List.of(booking, bookingWaitingStatus)));

        assertThrows(BookingBadRequestException.class,
                () -> bookingService.getAllBookingByUser(bookerUser.getId(), StateStatus.ERROR, 0, 2));
    }

    @Test
    void getAllBookingByOwnerId() {
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(Optional.of(List.of(booking, bookingWaitingStatus)));
        when(userService.getById(anyLong())).thenReturn(ownerUser);

        List<Booking> bookings = bookingService.getAllBookingByOwner(ownerUser.getId(), StateStatus.ALL,
                0, 2);

        assertNotEquals(bookings, null);
        assertEquals(bookings.size(), 2);

        verify(bookingRepository, times(1)).findAllByItemOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllBookingByOwnerShouldBookingNotFoundExceptionWhenBookingsIsNull() {
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any(Pageable.class))).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getAllBookingByOwner(ownerUser.getId(), StateStatus.ALL, 0, 2));
    }

}