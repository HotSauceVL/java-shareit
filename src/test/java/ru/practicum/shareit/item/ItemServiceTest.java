package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.BookingServiceForItem;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exception.AccessErrorException;
import ru.practicum.shareit.error.exception.ItemNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentsRepository;
import ru.practicum.shareit.item.dao.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.requests.service.ItemRequestCreator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingServiceForItem bookingService;

    @Mock
    private UserService userService;

    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private ItemRequestCreator itemRequestCreator;

    private final long userId = 1;
    private final long itemId = 1;
    private final long userIdWrong = 99L;

    private final User user = User.builder()
            .id(userId)
            .name("User")
            .email("user@yandex.ru")
            .build();

    private final Item item = Item.builder()
            .id(itemId)
            .name("Item for test")
            .description("Item description")
            .available(true)
            .owner(user)
            .bookings(new ArrayList<>())
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(itemId)
            .name("Item")
            .description("Item description")
            .available(true)
            .build();



    ItemService itemService;

    @BeforeEach
    void init() {
        itemService = new ItemServiceImpl(itemRepository, userService, bookingService,
                commentsRepository, itemRequestCreator);
    }

    @Test
    void getByIdShouldThrowItemNotFoundExceptionWhenRepositoryReturnEmpty() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getById(itemId, userId));
    }

    @Test
    void getByIdShouldReturnItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Item result = itemService.getById(itemId, userId);

        verify(itemRepository, times(1)).findById(anyLong());
        assertNotNull(result);
        assertEquals(item, result);
    }

    @Test
    void getAllByUserIdShouldReturnItem() {
        when(userService.getById(anyLong())).thenReturn(user);
        when(itemRepository.findByOwner(any(), any())).thenReturn((List.of(item)));

        List<Item> result = itemService.getAllByUserId(userId, 0, 2);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getOwner(), result.get(0).getOwner());
        verify(itemRepository, times(1)).findByOwner(any(), any());
    }

    @Test
    void addShouldReturnItem() {
        when(itemRepository.save(any())).thenReturn(item);

        Item result = itemService.add(anyLong(), itemDto);

        assertNotNull(result);
        assertEquals(item, result);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateShouldThrowItemNotFoundExceptionWhenRepositoryReturnEmpty() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.update(userId, itemId, itemDto));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateShouldThrowAccessErrorExceptionWhenWrongOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(AccessErrorException.class, () -> itemService.update(userIdWrong, itemId, itemDto));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateShouldReturnItem() {
        itemDto.setName("Updated item");

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(returnsFirstArg());

        Item result = itemService.update(userId, itemId, itemDto);

        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(any());
        assertNotNull(result);
        assertEquals("Updated item", result.getName());
    }

    @Test
    void searchByTextShouldReturnItem() {
        when(itemRepository.searchByText(any(), any())).thenReturn(List.of(item));

        List<Item> result = itemService.searchByText("test", 0, 2);

        verify(itemRepository, times(1)).searchByText(any(), any());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item, result.get(0));
    }

    @Test
    void addCommentShouldReturnItem() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .item(itemId)
                .authorName(user.getName())
                .text("Комментарий")
                .created(LocalDateTime.now())
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.checkBooking(anyLong(), anyLong(), any())).thenReturn(true);
        when(commentsRepository.save(any())).thenAnswer(returnsFirstArg());
        when(userService.getById(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Comment comment = CommentMapper.toComment(commentDto, user, item);

        Comment result = itemService.addComment(userId, itemId, commentDto);

        verify(commentsRepository, times(1)).save(comment);

        assertNotNull(result);
        assertEquals(comment, result);
    }

    @Test
    void deleteShouldDelete() {

        itemRepository.deleteById(item.getId());

        verify(itemRepository, Mockito.times(1)).deleteById(item.getId());
    }

}
