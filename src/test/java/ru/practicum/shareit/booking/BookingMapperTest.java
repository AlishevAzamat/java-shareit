package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {
    private final BookingMapper bookingMapper = new BookingMapper();

    @Test
    @DisplayName("Маппер toBookingDto")
    void Booking_compareResult_toBookingDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .status(Status.WAITING)
                .item(Item.builder().available(true).description("desc").name("name").build())
                .booker(User.builder().id(1L).name("name").email("user@mail").build())
                .build();
        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertEquals(booking.getEnd(), bookingDto.getEnd(), "end не сохроняется в dto");
        assertEquals(booking.getStart(), bookingDto.getStart(), "start не сохроняется в dto");
        assertEquals(booking.getId(), bookingDto.getId(), "id не сохроняется в dto");
        assertEquals(booking.getStatus(), bookingDto.getStatus(), "status не сохроняется в dto");
        assertEquals(booking.getBooker(), bookingDto.getBooker(), "booker не сохроняется в dto");
        assertEquals(booking.getItem(), bookingDto.getItem(), "item не сохроняется в dto");
    }

    @Test
    @DisplayName("Маппер toItemsBookingDto")
    void Booking_compareResult_toItemsBookingDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(User.builder().id(1L).name("name").email("user@mail").build())
                .build();
        BookingItemDto itemsBookingDto = bookingMapper.toItemsBookingDto(booking);

        assertEquals(booking.getId(), itemsBookingDto.getId(), "id не сохроняется в itemBookingDto");
        assertEquals(booking.getBooker().getId(), itemsBookingDto.getBookerId(), "user не сохроняется в itemBookingDto");
    }

    @Test
    @DisplayName("Маппер toBooking")
    void Booking_compareResult_toBooking() {
        BookingDto bookingDto = BookingDto.builder().start(LocalDateTime.now()).end(LocalDateTime.now()).build();
        Booking booking = bookingMapper.toBooking(bookingDto);

        assertEquals(bookingDto.getStart(), booking.getStart(), "start  не сохроняется в model");
        assertEquals(bookingDto.getEnd(), booking.getEnd(), "end  не сохроняется в model");

    }
}