package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {
    User user = User.builder().id(1L).build();

    @Test
    void testEquals() {
        assertTrue(user.equals(User.builder().id(1L).build()));
    }

    @Test
    void testHashCode() {
        User user1 = User.builder().id(1L).build();

        assertEquals(user1.hashCode(), user.hashCode());
    }
}