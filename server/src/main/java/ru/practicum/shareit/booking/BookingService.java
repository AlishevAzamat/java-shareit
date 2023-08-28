package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto create(long userId, BookingDto booking);

    BookingDto update(long userId, long bookingId, Boolean approved);

    BookingDto getById(long userId, long bookingId);

    List<BookingDto> getAllByUser(long userId, String state, int from, int size);

    List<BookingDto> getAllByOwner(long ownerId, String state, int from, int size);
}