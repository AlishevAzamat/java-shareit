package ru.practicum.shareit.booking;

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
    private BookingMapper mapper = mock(BookingMapper.class);

    private final ItemService itemService = mock(ItemService.class);

    private final UserService userService = mock(UserService.class);

    private final BookingRepository repository = mock(BookingRepository.class);

    private BookingService service = new BookingServiceImpl(mapper, itemService, userService, repository);

    private final BookingDto bookingDto = BookingDto.builder().end(LocalDateTime.now()).start(LocalDateTime.now()).itemId(1L).build();

    @Test
    void createExceptionWhenAvailableIsFalse() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .available(false)
                        .name("name")
                        .description("description")
                        .build());
        when(mapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now())
                        .start(LocalDateTime.now())
                        .build());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void createExceptionWhenOwner() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .owner(User.builder().id(1L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build());
        when(mapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now())
                        .start(LocalDateTime.now())
                        .build());

        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            service.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void validateTimeExceptionEndBeforeStart() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build());
        when(mapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now().minusDays(1))
                        .start(LocalDateTime.now())
                        .build());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void validateTimeExceptionEndEqualsStart() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build());
        when(mapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now())
                        .start(LocalDateTime.now())
                        .build());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void validateTimeExceptionStartBeforeNow() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build());
        when(mapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now().plusHours(1))
                        .start(LocalDateTime.now().minusDays(1))
                        .build());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getBookingExceptionUnknown() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            service.getById(1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getBookingExceptionNegative() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            service.getById(1, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getBookingExceptionNoOwnerItemAndNoBooker() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(Booking.builder()
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
            service.getById(1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserFromNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByUser(1, "ALL", -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserSizeNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByUser(1, "ALL", 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserSizeZero() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByUser(1, "ALL", 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserStateUnknown() {
        Throwable thrown = assertThrows(UnknownStateException.class, () -> {
            service.getAllByUser(1, "qwq", 0, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByOwnerFromNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByOwner(1, "ALL", -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByOwnerSizeNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByOwner(1, "ALL", 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByOwnerSizeZero() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByOwner(1, "ALL", 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByOwnerStateUnknown() {
        Throwable thrown = assertThrows(UnknownStateException.class, () -> {
            service.getAllByOwner(1, "qwq", 0, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateApprovedIsNull() {
        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            service.update(1, 1, null);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateNoOwner() {
        when(repository.findById(any()))
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
        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            service.update(1, 1, false);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateStatusApproved() {
        when(repository.findById(any()))
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
            service.update(1, 1, false);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateStatusRejected() {
        when(repository.findById(any()))
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
            service.update(1, 1, false);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getByIdWhenOwnerItemWithMapper() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
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
        when(repository.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = service.getById(1, 1);

        assertNotNull(bookingDto, "null при получении (возможно проверить работу маппера)");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Не возвращается нужный start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Не возвращает нужный end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "Не возвращает нужный item");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void getByIdWhenBookerWithMapper() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
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
        when(repository.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = service.getById(2, 1);

        assertNotNull(bookingDto, "null при получении (возможно проверить работу маппера)");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Не возвращается нужный start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Не возвращает нужный end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "Не возвращает нужный item");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void updateWhenApprovedTrueWithMapper() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
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
        when(repository.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = service.update(1, 1, true);

        assertNotNull(bookingDto, "null при получении (возможно проверить работу маппера)");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Не возвращается нужный start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Не возвращает нужный end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "Не возвращает нужный item");
        assertEquals(Status.APPROVED, bookingDto.getStatus(), "Не возвращает нужный status");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void updateWhenApprovedFalseWithMapper() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
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
        when(repository.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = service.update(1, 1, false);

        assertNotNull(bookingDto, "null при получении (возможно проверить работу маппера)");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Не возвращается нужный start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Не возвращает нужный end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "Не возвращает нужный item");
        assertEquals(Status.REJECTED, bookingDto.getStatus(), "Не возвращает нужный status");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void getAllByOwnerStateAll() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(repository.findByOwnerId(anyLong(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByOwner(1, "ALL", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }


    @Test
    void getAllByOwnerStateFUTURE() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(repository.findByOwnerIdAndStatusIn(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByOwner(1, "FUTURE", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void getAllByOwnerStateREJECTED() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(repository.findByOwnerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByOwner(1, "REJECTED", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void getAllByOwnerStateWAITING() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(repository.findByOwnerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByOwner(1, "WAITING", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void getAllByOwnerStateCURRENT() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(repository.findByOwnerIdCurrent(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByOwner(1, "CURRENT", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void getAllByOwnerStatePAST() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(repository.findByOwnerIdPast(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByOwner(1, "PAST", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void getAllByUserStateAll() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(repository.findByBookerId(anyLong(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByUser(1, "ALL", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }


    @Test
    void getAllByUserStateFUTURE() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(repository.findByBookerIdAndStatusIn(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByUser(1, "FUTURE", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void getAllByUserStateREJECTED() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(repository.findByBookerIdAndStatusIs(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByUser(1, "REJECTED", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void getAllByUserStateWAITING() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(repository.findByBookerIdAndStatusIs(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByUser(1, "WAITING", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void getAllByUserStateCURRENT() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(repository.findByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByUser(1, "CURRENT", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void getAllByUserStatePAST() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        when(userService.getById(anyLong())).thenReturn(User.builder().build());
        when(repository.findByBookerIdAndEndBefore(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> bookingDtos = service.getAllByUser(1, "PAST", 0, 1);

        assertNotNull(bookingDtos, "null при получении (возможно проверить работу маппера)");
        assertEquals(0, bookingDtos.size(), "Не возвращается список");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

}