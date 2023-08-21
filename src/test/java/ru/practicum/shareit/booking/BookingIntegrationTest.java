package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class BookingIntegrationTest {

    private static final long BOOKING_ID_ONE = 1L;
    private static final long BOOKING_ID_TWO = 2L;
    private static final long USER_ID_ONE = 1L;
    private static final long USER_ID_TWO = 2L;
    private static final int FROM = 0;
    private static final int SIZE = 10;
    @Autowired
    private ItemController itemController;
    @Autowired
    private UserController userController;
    @Autowired
    private BookingController bookingController;

    User user = User.builder().id(1L).name("name").email("user@mail.ru").build();
    private ItemDto itemDtoTestOne = ItemDto.builder()
            .id(0L)
            .name("TestName1")
            .description("testDescription1")
            .owner(user)
            .available(true)
            .build();

    private ItemDto itemDtoTestTwo = ItemDto.builder()
            .id(0L)
            .name("TestName2")
            .description("testDescription2")
            .owner(user)
            .available(true)
            .build();

    private UserDto userDtoTestOne = UserDto.builder()
            .id(0L)
            .name("TestName")
            .email("test@test.test")
            .build();

    private UserDto userDtoTestTwo = UserDto.builder()
            .id(0L)
            .name("TestName2")
            .email("test@test.test2")
            .build();

    private final BookingDto bookingDtoResponse1 = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.of(2024, 1, 1, 1, 1))
            .end(LocalDateTime.of(2025, 1, 1, 1, 1))
            .build();

    private final BookingDto bookingDtoResponse2 = BookingDto.builder()
            .itemId(2L)
            .start(LocalDateTime.of(2024, 1, 1, 1, 1))
            .end(LocalDateTime.of(2025, 1, 1, 1, 1))
            .build();

    @BeforeEach
    void setUp() {
        userDtoTestOne = userController.add(userDtoTestOne);
        userDtoTestTwo = userController.add(userDtoTestTwo);
        itemDtoTestOne = itemController.add(USER_ID_ONE, itemDtoTestOne);
        itemDtoTestTwo = itemController.add(USER_ID_TWO, itemDtoTestTwo);
    }

    @Test
    @DisplayName("Проверяем метод Post контроллера booking.")
    void createBooking_compareResult_whenCorrect() {
        BookingDto bookingDto = bookingController.createBooking(USER_ID_TWO, bookingDtoResponse1);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.getId(), "id должен быть 1");
    }

    @Test
    @DisplayName("Проверяем метод GET owner (все id) контроллера booking.")
    void getBookingOwner_compareResult_whenCorrect() {
        bookingController.createBooking(USER_ID_TWO, bookingDtoResponse1);
        bookingController.createBooking(USER_ID_ONE, bookingDtoResponse2);

        List<BookingDto> bookingDtoRequests1 = bookingController.getAllBookingByOwner(USER_ID_ONE, "ALL", FROM, SIZE);
        List<BookingDto> bookingDtoRequests2 = bookingController.getAllBookingByOwner(USER_ID_ONE, "ALL", FROM, SIZE);

        assertEquals(1, bookingDtoRequests1.size(), "Размер списка должен быть равен 1.");
        assertEquals(bookingDtoResponse1.getItemId(), bookingDtoRequests1.get(0).getItem().getId(), "Вещи должны совпадать.");
        assertEquals(1, bookingDtoRequests2.size(), "Размер списка должен быть равен 1.");
        assertEquals(bookingDtoResponse1.getItemId(), bookingDtoRequests1.get(0).getItem().getId(), "Вещи должны совпадать.");
    }


    @Test
    @DisplayName("Проверяем метод GET(id) контроллера booking. Проверяем метод POST контроллера booking.")
    void getBooking_compareResult_whenCorrect() {
        bookingController.createBooking(USER_ID_TWO, bookingDtoResponse1);
        bookingController.createBooking(USER_ID_ONE, bookingDtoResponse2);

        BookingDto bookingDtoRequests1 = bookingController.getBooking(USER_ID_TWO, BOOKING_ID_ONE);
        assertEquals(1, bookingDtoRequests1.getId(), "Id должен совпадать.");
        assertEquals(USER_ID_TWO, bookingDtoRequests1.getBooker().getId(), "Id должен совпадать.");
        assertEquals(itemDtoTestOne.getId(), bookingDtoRequests1.getItem().getId(), "Id должен совпадать.");
        assertEquals(Status.WAITING, bookingDtoRequests1.getStatus(), "Статус должен совпадать.");

        BookingDto bookingDtoRequests2 = bookingController.getBooking(USER_ID_ONE, BOOKING_ID_TWO);
        assertEquals(2, bookingDtoRequests2.getId(), "Id должен совпадать.");
        assertEquals(USER_ID_ONE, bookingDtoRequests2.getBooker().getId(), "Id должен совпадать.");
        assertEquals(itemDtoTestTwo.getId(), bookingDtoRequests2.getItem().getId(), "Id должен совпадать.");
        assertEquals(Status.WAITING, bookingDtoRequests2.getStatus(), "Статус должен совпадать.");

        assertNotEquals(bookingDtoRequests1, bookingDtoRequests2, "Объекты не должны совпадать");
    }

    @Test
    @DisplayName("Проверяем метод PATCH контроллера booking.")
    void updateBooking_compareResult_whenCorrect() {
        BookingDto bookingDtoRequests;
        bookingController.createBooking(USER_ID_TWO, bookingDtoResponse1);
        bookingController.createBooking(USER_ID_ONE, bookingDtoResponse2);

        bookingController.updateBooking(BOOKING_ID_ONE, USER_ID_ONE, true);

        bookingDtoRequests = bookingController.getBooking(BOOKING_ID_ONE, USER_ID_ONE);
        assertEquals(Status.APPROVED, bookingDtoRequests.getStatus(), "Статус должен совпадать.");

        bookingController.updateBooking(BOOKING_ID_TWO, USER_ID_TWO, false);

        bookingDtoRequests = bookingController.getBooking(BOOKING_ID_TWO, USER_ID_TWO);
        assertEquals(Status.REJECTED, bookingDtoRequests.getStatus(), "Статус должен совпадать.");
    }
}
