package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.ParameterNotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {
    private BookingMapper bookingMapper = mock(BookingMapper.class);

    private final ItemService itemService = mock(ItemService.class);

    private final UserService userService = mock(UserService.class);

    private final BookingRepository bookingRepository = mock(BookingRepository.class);

    private BookingService bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);

    private final BookingDto bookingDto = BookingDto.builder().end(LocalDateTime.now()).start(LocalDateTime.now()).itemId(1L).build();

    @Test
    @DisplayName("Ошибка бронирования при занятом предмете")
    void createBooking_throwValidationException_whenAvailableIsFalse() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .available(false)
                        .name("name")
                        .description("description")
                        .build());
        when(bookingMapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now())
                        .start(LocalDateTime.now())
                        .build());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            bookingService.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Ошибка бронирования при том что он хозяин предмета")
    void createBooking_throwIncorrectParameterException_whenOwnerIsMaster() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .owner(User.builder().id(1L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build());
        when(bookingMapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now())
                        .start(LocalDateTime.now())
                        .build());

        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            bookingService.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Ошибка валидации при том что конец бронирования раньше начала")
    void createBooking_throwValidationException_whenEndBeforeStart() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build());
        when(bookingMapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now().minusDays(1))
                        .start(LocalDateTime.now())
                        .build());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            bookingService.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Ошибка валидации при том что время создания и закрытия равны")
    void createBooking_throwValidationException_whenEndEqualsStart() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build());
        when(bookingMapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now())
                        .start(LocalDateTime.now())
                        .build());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            bookingService.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Ошибка валидации при том что начала времени раньше реального")
    void createBooking_throwValidationException_whenStartBeforeRealTime() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build());
        when(bookingMapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now().plusHours(1))
                        .start(LocalDateTime.now().minusDays(1))
                        .build());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            bookingService.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Бронь не найдена")
    void getBooking_throwParameterNotFoundException_whenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            bookingService.getById(1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Запрос брони при -1")
    void getBooking_throwIncorrectParameterException_whenBookingNegative() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            bookingService.getById(1, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Ошибка валидации при заросе брони")
    void getBooking_throwIncorrectParameterException_whenNoOwnerItemAndNoBooker() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(Booking.builder()
                .booker(User.builder().id(2L).email("user@mail").name("name").build())
                .item(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build())
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now().plusHours(1))
                .build()));
        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            bookingService.getById(1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Запрос всей брони при -1")
    void getAllByUser_throwIllegalArgumentException_whenFromNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getAllByUser(1, "ALL", -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Запрос всей брони при пагинации -1")
    void getAllByUser_throwIllegalArgumentException_whenSizeNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getAllByUser(1, "ALL", 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Запрос всей брони при пагинации 0")
    void getAllByUser_throwIllegalArgumentException_whenSizeZero() {
        Throwable thrown = assertThrows(ArithmeticException.class, () -> {
            bookingService.getAllByUser(1, "ALL", 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Запрос всей брони при неправильном статусе")
    void getAllByUser_throwUnknownStateException_whenStateUnknown() {
        Throwable thrown = assertThrows(UnknownStateException.class, () -> {
            bookingService.getAllByUser(1, "qwq", 0, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Запрос всей брони хозяина при -1")
    void getAllByOwner_throwIllegalArgumentException_whenFromNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getAllByOwner(1, "ALL", -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Запрос всей брони хозяина при пагинации -1")
    void getAllByOwner_throwIllegalArgumentException_whenSizeNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getAllByOwner(1, "ALL", 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Запрос всей брони хозяина при пагинации 0")
    void getAllByOwner_throwArithmeticException_whenSizeZero() {
        Throwable thrown = assertThrows(ArithmeticException.class, () -> {
            bookingService.getAllByOwner(1, "ALL", 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Запрос всей брони хозяина при неправильном статусе")
    void getAllByOwner_throwUnknownStateException_whenStateUnknown() {
        Throwable thrown = assertThrows(UnknownStateException.class, () -> {
            bookingService.getAllByOwner(1, "qwq", 0, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Неверное обновление бронирования")
    void updateBooking_throwUnknownStateException_whenApprovedIsNull() {
        Throwable thrown = assertThrows(UnknownStateException.class, () -> {
            bookingService.update(1, 1, null);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Неверное обновление бронирования неверных данных")
    void updateBooking_throwParameterNotFoundException_whenNoOwner() {
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.of(Booking.builder()
                        .start(LocalDateTime.now().plusHours(1))
                        .end(LocalDateTime.now().plusDays(1))
                        .item(Item.builder()
                                .owner(User.builder().id(2L).name("name").email("user@mail").build())
                                .available(true)
                                .name("name")
                                .description("description")
                                .build())
                        .build()));
        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            bookingService.update(1, 1, false);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Неверное обновление бронирования при неверном статусе")
    void updateBooking_throwValidationException_whenStatusApproved() {
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.of(Booking.builder()
                        .start(LocalDateTime.now().plusHours(1))
                        .end(LocalDateTime.now().plusDays(1))
                        .item(Item.builder()
                                .owner(User.builder().id(1L).name("name").email("user@mail").build())
                                .available(true)
                                .name("name")
                                .description("description")
                                .build())
                        .status(Status.APPROVED)
                        .build()));
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            bookingService.update(1, 1, false);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Неверное обновление бронирования при неверном статусе")
    void updateBooking_throwValidationException_whenStatusRejected() {
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.of(Booking.builder()
                        .start(LocalDateTime.now().plusHours(1))
                        .end(LocalDateTime.now().plusDays(1))
                        .item(Item.builder()
                                .owner(User.builder().id(1L).name("name").email("user@mail").build())
                                .available(true)
                                .name("name")
                                .description("description")
                                .build())
                        .status(Status.REJECTED)
                        .build()));
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            bookingService.update(1, 1, false);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Запрос брони")
    void bookingGetById_compareResult_whenOwnerItemWithMapper() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        Booking booking = Booking.builder()
                .id(1L)
                .status(Status.WAITING)
                .booker(User.builder().id(2L).name("name").email("user@mail").build())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(Item.builder()
                        .owner(User.builder().id(1L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build())
                .build();
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getById(1, 1);

        assertNotNull(bookingDto, "null при получении (возможно проверить работу маппера)");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Не возвращается нужный start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Не возвращает нужный end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "Не возвращает нужный item");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Запрос брони")
    void BookingGetById_compareResult_whenBookerWithMapper() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        Booking booking = Booking.builder()
                .booker(User.builder().id(2L).name("name").email("user@mail").build())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(Item.builder()
                        .owner(User.builder().id(1L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build())
                .build();
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getById(2, 1);

        assertNotNull(bookingDto, "null при получении (возможно проверить работу маппера)");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Не возвращается нужный start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Не возвращает нужный end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "Не возвращает нужный item");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Обновление брони")
    void updateBooking_compareResult_whenApprovedTrueWithMapper() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        Booking booking = Booking.builder()
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(Item.builder()
                        .owner(User.builder().id(1L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build())
                .build();
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.update(1, 1, true);

        assertNotNull(bookingDto, "null при получении (возможно проверить работу маппера)");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Не возвращается нужный start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Не возвращает нужный end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "Не возвращает нужный item");
        assertEquals(Status.APPROVED, bookingDto.getStatus(), "Не возвращает нужный status");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Обновление брони")
    void updateBooking_compareResult_whenApprovedFalseWithMapper() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        Booking booking = Booking.builder()
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(Item.builder()
                        .owner(User.builder().id(1L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build())
                .build();
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.update(1, 1, false);

        assertNotNull(bookingDto, "null при получении (возможно проверить работу маппера)");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Не возвращается нужный start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Не возвращает нужный end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "Не возвращает нужный item");
        assertEquals(Status.REJECTED, bookingDto.getStatus(), "Не возвращает нужный status");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Сверка количества бронирования All")
    void bookingGetAll_compareResult_whenOwnerStateAll() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(bookingRepository.findByOwnerId(anyLong(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = bookingService.getAllByOwner(1, "ALL", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }


    @Test
    @DisplayName("Сверка количества бронирования FUTURE")
    void bookingGetAllByOwner_compareResult_whenStateFuture() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(bookingRepository.findByOwnerIdAndStatusIn(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = bookingService.getAllByOwner(1, "FUTURE", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Сверка количества бронирования REJECTED")
    void bookingGetAllByOwner_compareResult_whenStateRejected() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(bookingRepository.findByOwnerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = bookingService.getAllByOwner(1, "REJECTED", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Сверка количества бронирования WAITING")
    void bookingGetAllByOwner_compareResult_whenStateWaiting() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(bookingRepository.findByOwnerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = bookingService.getAllByOwner(1, "WAITING", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Сверка количества бронирования CURRENT")
    void bookingGetAllByOwner_compareResult_whenStateCurrent() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(bookingRepository.findByOwnerIdCurrent(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = bookingService.getAllByOwner(1, "CURRENT", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Сверка количества бронирования PAST")
    void bookingGetAllByOwner_compareResult_whenStatePast() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(bookingRepository.findByOwnerIdPast(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = bookingService.getAllByOwner(1, "PAST", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Сверка количества бронирования пользователя ALL")
    void bookingGetAllByUser_compareResult_whenStateAll() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(bookingRepository.findByBookerId(anyLong(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = bookingService.getAllByUser(1, "ALL", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }


    @Test
    @DisplayName("Сверка количества бронирования пользователя FUTURE")
    void bookingGetAllByUser_compareResult_whenStateFuture() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(bookingRepository.findByBookerIdAndStatusIn(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = bookingService.getAllByUser(1, "FUTURE", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Сверка количества бронирования пользователя REJECTED")
    void bookingGetAllByUser_compareResult_whenStateRejected() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(bookingRepository.findByBookerIdAndStatusIs(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = bookingService.getAllByUser(1, "REJECTED", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Сверка количества бронирования пользователя WAITING")
    void bookingGetAllByUser_compareResult_whenStateWaiting() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(bookingRepository.findByBookerIdAndStatusIs(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = bookingService.getAllByUser(1, "WAITING", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Сверка количества бронирования пользователя CURRENT")
    void bookingGetAllByUser_compareResult_whenStateCurrent() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = bookingService.getAllByUser(1, "CURRENT", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

    @Test
    @DisplayName("Сверка количества бронирования пользователя PAST")
    void bookingGetAllByUser_compareResult_whenStatePast() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(bookingRepository.findByBookerIdAndEndBefore(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = bookingService.getAllByUser(1, "PAST", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingMapper, itemService, userService, bookingRepository);
    }

}