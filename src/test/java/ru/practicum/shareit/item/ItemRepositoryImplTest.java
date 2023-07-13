package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRepositoryImplTest {
    private final ItemRepository repository = new ItemRepositoryImpl();

    @BeforeEach
    void beforeEach() {
        repository.getAll().clear();
    }

    @Test
    void add() {
        Item item = repository.add(Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(1, item.getId());
        assertEquals(1, repository.getAll().size());
    }

    @Test
    void update() {
        Item item = repository.add(Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        item.setName("Дрель");
        Item update = repository.update(item);

        assertEquals(1, update.getId());
        assertEquals("Дрель", update.getName());
        assertEquals(1, repository.getAll().size());
    }

    @Test
    void delete() {
        Item item = repository.add(Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        repository.delete(item.getId());

        assertEquals(0, repository.getAll().size());
    }

    @Test
    void getAll() {
        assertEquals(0, repository.getAll().size());

        repository.add(Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(1, repository.getAll().size());

        repository.delete(1);

        assertEquals(0, repository.getAll().size());
    }
}