package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("Сохранение вещи")
    void saveItem_compareResult_whenObjectCorrect() {
        Item item = Item.builder().id(7L).name("text").description("text").available(true).build();
        Item item2 = Item.builder().id(7L).name("qqq").description("asd").available(true).build();

        Item item1 = itemRepository.save(item);
        itemRepository.save(item2);

        List<Item> items = itemRepository.search("text", PageRequest.of(0, 3))
                .stream().collect(Collectors.toList());

        assertEquals(1, items.size(), "возвращает не 1 нужный запрос");
        assertEquals(item1, items.get(0), "возвращает не нужный запрос");
    }
}