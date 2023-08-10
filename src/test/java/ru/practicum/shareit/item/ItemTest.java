package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTest {
    Item item = Item.builder().id(1L).build();

    @Test
    void testEquals() {
        assertTrue(item.equals(Item.builder().id(1L).build()));
    }

    @Test
    void testHashCode() {
        Item item1 = Item.builder().id(1L).build();

        assertEquals(item1.hashCode(), item.hashCode());
    }
}