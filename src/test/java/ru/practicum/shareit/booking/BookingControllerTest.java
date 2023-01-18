package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dao.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.AccessErrorException;
import ru.practicum.shareit.error.exception.BookingNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    private final String baseUrl = "/bookings";
    @MockBean
    private BookingService bookingService;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private ItemService itemService;
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    private final long itemId = 1;
    private final LocalDateTime date = LocalDateTime.now();
    private final User ownerUser = User.builder()
            .id(1L)
            .name("Owner")
            .email("owner@yandex.ru").build();
    private final User bookerUser = User.builder()
            .id(2L)
            .name("Booker")
            .email("booker@yandex.ru").build();
    private final Item item = Item.builder()
            .id(itemId)
            .name("Item for test")
            .description("Item description")
            .available(true)
            .owner(ownerUser)
            .bookings(new ArrayList<>())
            .build();

    private final BookingInputDto bookingInputDto = BookingInputDto.builder()
            .id(1L)
            .startDate(date.minusDays(1))
            .endDate(date.plusDays(2))
            .itemId(item.getId())
            .build();

    private final Booking booking = Booking.builder()
            .id(bookingInputDto.getId())
            .start(bookingInputDto.getStartDate())
            .end(bookingInputDto.getEndDate())
            .item(item)
            .booker(bookerUser)
            .status(BookingStatus.APPROVED)
            .build();

    private final BookingInputDto bookingInputDtoWaitingStatus = BookingInputDto.builder()
            .id(2L)
            .startDate(date.plusDays(1))
            .endDate(date.plusDays(2))
            .itemId(item.getId())
            .build();
    private final Booking bookingWaitingStatus = Booking.builder()
            .id(bookingInputDtoWaitingStatus.getId())
            .start(bookingInputDtoWaitingStatus.getStartDate())
            .end(bookingInputDtoWaitingStatus.getEndDate())
            .item(item)
            .booker(bookerUser)
            .status(BookingStatus.WAITING)
            .build();

    @DisplayName("Test for add metod should return answer 404 when booker = owner")
    @Test
    void addBookingShouldAnswer404WhenUserIsOwnerTheItem() throws Exception {
        when(bookingService.add(anyLong(), any())).thenThrow(new AccessErrorException(""));

        mockMvc.perform(post(baseUrl)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", ownerUser.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookingShouldReturnBookingWithStatusWAITING() throws Exception {
        when(bookingService.add(anyLong(), any())).thenReturn(bookingWaitingStatus);

        mockMvc.perform(post(baseUrl)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingInputDtoWaitingStatus))
                        .header("X-Sharer-User-Id", bookerUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void bookingConfirmationShould404WhenBookingNotFound() throws Exception {
        when(bookingService.bookingConfirmation(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new BookingNotFoundException(""));

        mockMvc.perform(patch(baseUrl + "/{bookingId}", 99L)
                        .header("X-Sharer-User-Id", ownerUser.getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookingConfirmationShould404WhenUserIsNotOwner() throws Exception {
        when(bookingService.bookingConfirmation(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new AccessErrorException(""));

        mockMvc.perform(patch(baseUrl + "/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", bookerUser.getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookingConfirmationShouldSetStatusToApproved() throws Exception {

        when(bookingService.bookingConfirmation(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);

        mockMvc.perform(patch(baseUrl + "/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", ownerUser.getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void bookingConfirmationShouldSetStatusToRejected() throws Exception {
        bookingWaitingStatus.setStatus(BookingStatus.REJECTED);
        when(bookingService.bookingConfirmation(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingWaitingStatus);

        mockMvc.perform(patch(baseUrl + "/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", ownerUser.getId())
                        .param("approved", String.valueOf(false)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REJECTED")));
    }

    @Test
    void getByIdShouldAnswer404WhenBookingNotFound() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenThrow(new BookingNotFoundException(""));

        mockMvc.perform(get(baseUrl + "/{bookingId}", 99L)
                        .header("X-Sharer-User-Id", bookerUser.getId())).andExpect(status().isNotFound());
    }

    @Test
    void getByIdShouldReturnBooking() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(booking);

        mockMvc.perform(get(baseUrl + "/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", bookerUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())));;
    }

    @Test
    void getAllBookingByUserShouldReturnBookings() throws Exception {
        when(bookingService.getAllBookingByUser(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        mockMvc.perform(get(baseUrl + "/")
                        .header("X-Sharer-User-Id", bookerUser.getId())
                        .param("state", String.valueOf(StateStatus.ALL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void getAllBookingByUserShouldMethodArgumentTypeMismatchException() throws Exception {
        when(bookingService.getAllBookingByUser(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        mockMvc.perform(get(baseUrl + "/")
                        .header("X-Sharer-User-Id", bookerUser.getId())
                        .param("state", "Wrong state"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllBookingByOwnerShouldReturnBookings() throws Exception {
        when(bookingService.getAllBookingByOwner(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        mockMvc.perform(get(baseUrl + "/owner")
                        .header("X-Sharer-User-Id", ownerUser.getId())
                        .param("state", String.valueOf(StateStatus.ALL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

}
