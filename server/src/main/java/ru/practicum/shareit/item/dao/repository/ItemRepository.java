package ru.practicum.shareit.item.dao.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(User owner, Pageable pageable);

    @Query(" select i from Item i " +
            "where lower(i.name) like lower(concat('%', ?1, '%')) " +
            " or lower(i.description) like lower(concat('%', ?1, '%'))")
    List<Item> searchByText(String text, Pageable pageable);

    Set<Item> findAllByItemRequest_Id(long itemRequestId);
}
