package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.error.exception.UserNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.dao.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final long userId = 1;
    private final long requestId = 1;
    private final long userIdWrong = 99L;

    private final User user = User.builder()
            .id(userId)
            .name("User")
            .email("user@yandex.ru")
            .build();

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(requestId)
            .description("Request description")
            .build();

    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(requestId)
            .description("Request description")
            .requestor(user)
            .createdTime(LocalDateTime.now())
            .build();


    @Test
    void addShouldReturnItemRequest() {
        when(requestRepository.save(any())).thenAnswer(returnsFirstArg());
        when(userService.getById(anyLong())).thenReturn(user);

        assertEquals(itemRequest.getId(), itemRequestService.add(userId, itemRequestDto).getId());

        verify(requestRepository, times(1)).save(any());
    }

    @Test
    void getAllItemRequestsByUserIdShouldThrowUserNotFoundExceptionWhenUserWrong() {
        when(userService.getById(anyLong())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> itemRequestService.getAllItemRequestsByUserId(userIdWrong));
    }

    @Test
    void getAllItemRequestsByUserIdShouldReturnItemRequest() {
        when(requestRepository.findAllByRequestor_Id(anyLong())).thenReturn(Optional.of(List.of(itemRequest)));

        assertEquals(List.of(itemRequest), itemRequestService.getAllItemRequestsByUserId(userId));

        verify(requestRepository, times(1)).findAllByRequestor_Id(userId);
    }

    @Test
    void getItemRequestByIdShouldThrowUserNotFoundExceptionWhenUserWrong() {
        when(userService.getById(anyLong())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> itemRequestService.getItemRequestById(requestId, userIdWrong));
    }

    @Test
    void getItemRequestByIdShouldThrowItemRequestNotFoundExceptionWhenItemRequestNotFound() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService
                .getItemRequestById(99L, userId));
    }

    @Test
    void getItemRequestByIdShouldReturnItemRequest() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        assertEquals(itemRequest, itemRequestService.getItemRequestById(requestId, userId));

        verify(requestRepository, times(1)).findById(requestId);
    }

    @Test
    void getAllItemRequestsCreatedAnotherUsersShouldThrowConflictExceptionExceptionWhenPageableWrong() {

        assertThrows(ConflictException.class, () -> itemRequestService
                .getAllItemRequestsCreatedAnotherUsers(userId, -1, 2));

        verifyNoMoreInteractions(requestRepository);
    }

    @Test
    void getAllItemRequestsCreatedAnotherUsersShouldReturnListOfItemRequests() {
        when(requestRepository.findAllByRequestor_IdIsNot(anyLong(), any()))
                .thenReturn(Optional.of(List.of(itemRequest)));

        assertEquals(List.of(itemRequest), itemRequestService
                .getAllItemRequestsCreatedAnotherUsers(userId, 0, 2));

        verify(requestRepository, times(1)).findAllByRequestor_IdIsNot(anyLong(), any());
    }

}
