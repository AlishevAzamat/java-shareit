package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingTest {
    Booking booking = Booking.builder().id(1L).build();

    @Test
    void testEquals() {
        assertTrue(booking.equals(Booking.builder().id(1L).build()));
    }

    @Test
    void testHashCode() {
        Booking booking1 = Booking.builder().id(1L).build();
        assertEquals(booking.hashCode(), booking1.hashCode());
    }
}