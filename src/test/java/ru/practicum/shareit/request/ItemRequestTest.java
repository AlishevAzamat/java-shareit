package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemRequestTest {
    ItemRequest itemRequest = ItemRequest.builder().id(1L).build();

    @Test
    void testEquals() {
        assertTrue(itemRequest.equals(ItemRequest.builder().id(1L).build()));
    }

    @Test
    void testHashCode() {
        ItemRequest itemRequest1 = ItemRequest.builder().id(1L).build();

        assertEquals(itemRequest1.hashCode(), itemRequest.hashCode());
    }
}