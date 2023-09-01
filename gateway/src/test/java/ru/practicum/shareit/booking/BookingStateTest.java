package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingState;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingStateTest {

    @DisplayName("Статус All")
    @Test
    void fromAll_compareResult_whenObjectIsCorrect() {
        Optional<BookingState> state = BookingState.from("all");

        assertTrue(state.isPresent());
    }

    @DisplayName("Статус Current")
    @Test
    void fromCurrent_compareResult_whenObjectIsCorrect() {
        Optional<BookingState> state = BookingState.from("cuRRent");

        assertTrue(state.isPresent());
    }

    @DisplayName("Статус Future")
    @Test
    void fromFuture_compareResult_whenObjectIsCorrect() {
        Optional<BookingState> state = BookingState.from("fuTure");

        assertTrue(state.isPresent());
    }

    @DisplayName("Статус Past")
    @Test
    void fromPast_compareResult_whenObjectIsCorrect() {
        Optional<BookingState> state = BookingState.from("pasT");

        assertTrue(state.isPresent());
    }

    @DisplayName("Статус Rejected")
    @Test
    void fromRejected_compareResult_whenObjectIsCorrect() {
        Optional<BookingState> state = BookingState.from("ReJeCTeD");

        assertTrue(state.isPresent());
    }

    @DisplayName("Статус Waiting")
    @Test
    void fromWaiting_compareResult_whenObjectIsCorrect() {
        Optional<BookingState> state = BookingState.from("WAITING");

        assertTrue(state.isPresent());
    }

    @DisplayName("Неизвестный статус")
    @Test
    void fromUnknown_compareResult_whenObjectIncorrect() {
        Optional<BookingState> state = BookingState.from("qwq");

        assertTrue(state.isEmpty());
    }

}