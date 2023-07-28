package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }

    public BookingItemDto toItemsBookingDto(Booking booking) {
        return BookingItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .build();
    }
}