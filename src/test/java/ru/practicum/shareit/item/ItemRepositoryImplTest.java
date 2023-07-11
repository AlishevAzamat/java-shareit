package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRepositoryImplTest {
    private final ItemRepository itemRepository = new ItemRepositoryImpl();

    @BeforeEach
    void beforeEach() {
        itemRepository.getAll().clear();
    }

    @Test
    void add() {
        Item item = itemRepository.add(Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(1, item.getId());
        assertEquals(1, itemRepository.getAll().size());
    }

    @Test
    void update() {
        Item item = itemRepository.add(Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        item.setName("Дрель");
        Item update = itemRepository.update(item);

        assertEquals(1, update.getId());
        assertEquals("Дрель", update.getName());
        assertEquals(1, itemRepository.getAll().size());
    }

    @Test
    void delete() {
        Item item = itemRepository.add(Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        itemRepository.delete(item.getId());

        assertEquals(0, itemRepository.getAll().size());
    }

    @Test
    void getAll() {
        assertEquals(0, itemRepository.getAll().size());

        itemRepository.add(Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(1, itemRepository.getAll().size());

        itemRepository.delete(1);

        assertEquals(0, itemRepository.getAll().size());
    }
}