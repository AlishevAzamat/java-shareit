package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    private LocalDate start;
    private LocalDate end;
    private User booker;
    private Item item;
    private Status status;
}
