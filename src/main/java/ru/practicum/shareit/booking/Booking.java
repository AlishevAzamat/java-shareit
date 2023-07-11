package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
