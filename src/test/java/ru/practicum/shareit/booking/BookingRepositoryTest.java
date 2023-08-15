package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(User.builder().id(1L).name("name").email("user@mail").build());
        item = itemRepository.save(Item.builder().id(1L).owner(user).name("name").description("desc").build());
    }

    @Test
    @DisplayName("Вывод бронирования")
    void saveBooking_compareResult_whenOwnerIdAndStatusIn() {
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();

        booking = bookingRepository.save(booking);
        bookingRepository.save(booking1);

        List<Booking> bookings = bookingRepository.findByOwnerIdAndStatusIn(user.getId(), Set.of(Status.WAITING, Status.APPROVED),
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    @DisplayName("Вывод бронирования без условия статуса")
    void saveBooking_compareResult_whenOwnerIdAndStatus() {
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();

        bookingRepository.save(booking);
        booking1 = bookingRepository.save(booking1);

        List<Booking> bookings = bookingRepository.findByOwnerIdAndStatus(user.getId(), Status.REJECTED,
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    @DisplayName("Вывод бронирования пользователя")
    void saveBooking_compareResult_whenOwnerId() {
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();

        booking = bookingRepository.save(booking);
        booking1 = bookingRepository.save(booking1);

        List<Booking> bookings = bookingRepository.findByOwnerId(user.getId(), PageRequest.of(0, 2))
                .stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(2, bookings.size(), "Не возвращает список с 2");
        assertEquals(booking, bookings.get(0), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(1), "Не возвращает список с 2");
    }

    @Test
    @DisplayName("Вывод текушего бронирования")
    void saveBooking_compareResult_whenOwnerIdCurrent() {
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now().plusDays(1)).start(LocalDateTime.now().minusDays(1)).build();

        bookingRepository.save(booking);
        booking1 = bookingRepository.save(booking1);

        List<Booking> bookings = bookingRepository.findByOwnerIdCurrent(user.getId(), LocalDateTime.now(),
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    @DisplayName("Вывод прошлого бронирования")
    void saveBooking_compareResult_whenOwnerIdPast() {
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now().plusDays(1)).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now().minusDays(1)).start(LocalDateTime.now()).build();

        bookingRepository.save(booking);
        booking1 = bookingRepository.save(booking1);

        List<Booking> bookings = bookingRepository.findByOwnerIdPast(user.getId(), LocalDateTime.now(),
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
    }
}