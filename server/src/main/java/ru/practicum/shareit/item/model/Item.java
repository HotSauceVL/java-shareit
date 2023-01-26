package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@Entity
@Table(name = "items")
@AllArgsConstructor
public class Item {

    public Item(long id, User owner, ItemRequest itemRequest, String name, String description, boolean available) {
        this.id = id;
        this.owner = owner;
        this.itemRequest = itemRequest;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false)
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "is_available")
    private boolean available;
    @Transient
    private List<Booking> bookings;
    @Transient
    private List<Comment> comments;
    @Transient
    private Booking lastBooking;
    @Transient
    private Booking nextBooking;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
