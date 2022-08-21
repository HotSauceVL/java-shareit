package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class Booking {
    private long id;
    private User booker;
    private Item item;
    private LocalDate start;
    private LocalDate end;
    private BookingStatus status;

}
