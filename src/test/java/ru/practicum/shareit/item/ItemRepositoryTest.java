package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dao.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {

    private final ItemRepository itemRepository;

    private final TestEntityManager testEntityManager;

    private final User user= User.builder()
            .name("Owner")
            .email("owner@yandex.ru").build();

    private final Item item = Item.builder()
            .name("Item")
            .description("Item description")
            .available(true)
            .owner(user)
            .bookings(new ArrayList<>())
            .build();

    private final Item anotherItem = Item.builder()
            .name("FindMe")
            .description("FindMe description")
            .available(true)
            .owner(user)
            .bookings(new ArrayList<>())
            .build();

    @Test
    void searchByTextShouldReturnListOfItem() {
        testEntityManager.getEntityManager().persist(user);
        testEntityManager.getEntityManager().persist(item);
        testEntityManager.getEntityManager().persist(anotherItem);

        List<Item> items = itemRepository.searchByText("FindMe", PageRequest.of(0, 2));

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), anotherItem.getName());
    }
}
