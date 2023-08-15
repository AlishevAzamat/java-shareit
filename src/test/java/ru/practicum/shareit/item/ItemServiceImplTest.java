package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingItemDto;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ParameterNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {
    private final UserService userService = mock(UserService.class);

    private final ItemRequestService itemRequestService = mock(ItemRequestService.class);

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private final BookingRepository bookingRepository = mock(BookingRepository.class);

    private final CommentRepository commentRepository = mock(CommentRepository.class);

    private ItemMapper itemMapper = mock(ItemMapper.class);

    private final BookingMapper bookingMapper = new BookingMapper();

    private final CommentMapper commentMapper = new CommentMapper();

    private ItemService service =
            new ItemServiceImpl(userService, itemRepository, itemMapper, bookingRepository,
                    commentRepository, commentMapper, bookingMapper, itemRequestService);

    @Test
    @DisplayName("Не существующий пользователь")
    void updateItem_throwParameterNotFoundException_whenNoOwner() {
        when(userService.getUser(anyLong()))
                .thenReturn(User.builder().name("name").email("user@mail").build());
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description").build()));

        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            service.update(1, 1, mock(ItemDto.class));
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Вывод всех вещей, валидация")
    void getAllItem_throwIllegalArgumentException_whenFromNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAll(1, -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Вывод всех вещей, отрицательная пагинация")
    void getAllItem_throwIllegalArgumentException_whenSizeNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAll(1, 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Вывод всех вещей, пагинация 0")
    void getAllItem_throwIllegalArgumentException_whenSizeZero() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAll(1, 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Поиск по словам вещей from -1")
    void searchItemText_throwIllegalArgumentException_whenFromNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.searchText(1, "text", -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Поиск по словам вещей пагинация -1")
    void searchItemText_throwIllegalArgumentException_whenSizeNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.searchText(1, "text", 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Поиск по словам вещей пагинация 0")
    void searchItem_throwIllegalArgumentException_whenTextSizeZero() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.searchText(1, "text", 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Поиск по словам вещей")
    void searchItemText_compareResult_whenStrIsEmpty() {
        List<ItemDto> items = service.searchText(1, "", 0, 1);

        assertEquals(0, items.size(), "Не возвращает пустой список при пустом тексте");
    }

    @Test
    @DisplayName("Добавления комента к не сушествующему бронирования")
    void addComment_throwValidationException_whenBookingIsEmpty() {
        when(bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.addComment(1, 1, mock(CommentDto.class));
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Вывод не существующего бронирования")
    void getItemById_throwParameterNotFoundException_whenUnknown() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            service.getItem(0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Вывод не существующего бронирования -1")
    void getItemById_throwParameterNotFoundException_whenNegative() {
        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            service.getItem(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Обновление описания и доступности вещи")
    void updateItem_compareResult_whenNameDescriptionAndAvailable() {
        itemMapper = new ItemMapper();
        service = new ItemServiceImpl(userService, itemRepository, itemMapper, bookingRepository,
                commentRepository, commentMapper, bookingMapper, itemRequestService);
        when(userService.getUser(anyLong())).thenReturn(User.builder().id(1L).build());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder()
                .owner(User.builder().id(1L).build())
                .name("name")
                .description("Desc")
                .available(false).build()));
        ItemDto update = ItemDto.builder().available(true).description("description").name("Pen").build();

        ItemDto itemDto = service.update(1, 1, update);

        assertNotNull(itemDto, "Null при возвращении (проверить работу маппера)");
        assertEquals(update.getAvailable(), itemDto.getAvailable(), "не сохроняет available");
        assertEquals(update.getDescription(), itemDto.getDescription(), "не сохроняет description");
        assertEquals(update.getName(), itemDto.getName(), "не сохроняет name");

        itemMapper = mock(ItemMapper.class);
        service = new ItemServiceImpl(userService, itemRepository, itemMapper, bookingRepository,
                commentRepository, commentMapper, bookingMapper, itemRequestService);
    }

    @Test
    @DisplayName("Обновление имени вещи")
    void updateItem_compareResult_whenName() {
        itemMapper = new ItemMapper();
        service = new ItemServiceImpl(userService, itemRepository, itemMapper, bookingRepository,
                commentRepository, commentMapper, bookingMapper, itemRequestService);
        when(userService.getUser(anyLong())).thenReturn(User.builder().id(1L).build());
        Item itemRepository = Item.builder()
                .owner(User.builder().id(1L).build())
                .name("name")
                .description("Desc")
                .available(false).build();
        when(this.itemRepository.findById(anyLong())).thenReturn(Optional.of(itemRepository));
        ItemDto update = ItemDto.builder().name("Pen").build();

        ItemDto itemDto = service.update(1, 1, update);

        assertNotNull(itemDto, "Null при возвращении (проверить работу маппера)");
        assertEquals(itemRepository.isAvailable(), itemDto.getAvailable(), "Изменяет available, но не должен");
        assertEquals(itemRepository.getDescription(), itemDto.getDescription(), "Изменяет description, но не должен");
        assertEquals(update.getName(), itemDto.getName(), "не сохроняет name");

        itemMapper = mock(ItemMapper.class);
        service = new ItemServiceImpl(userService, this.itemRepository, itemMapper, bookingRepository,
                commentRepository, commentMapper, bookingMapper, itemRequestService);
    }

    @Test
    @DisplayName("Обновление описания вещи")
    void updateItem_compareResult_whenDescription() {
        itemMapper = new ItemMapper();
        service = new ItemServiceImpl(userService, itemRepository, itemMapper, bookingRepository,
                commentRepository, commentMapper, bookingMapper, itemRequestService);
        when(userService.getUser(anyLong())).thenReturn(User.builder().id(1L).build());
        Item itemRepository = Item.builder()
                .owner(User.builder().id(1L).build())
                .name("name")
                .description("Desc")
                .available(false).build();
        when(this.itemRepository.findById(anyLong())).thenReturn(Optional.of(itemRepository));
        ItemDto update = ItemDto.builder().available(true).description("description").name("Pen").build();

        ItemDto itemDto = service.update(1, 1, update);

        assertNotNull(itemDto, "Null при возвращении (проверить работу маппера)");
        assertEquals(itemRepository.isAvailable(), itemDto.getAvailable(), "Изменяет available, но не должен");
        assertEquals(update.getDescription(), itemDto.getDescription(), "не сохроняет description");
        assertEquals(itemRepository.getName(), itemDto.getName(), "Изменяет name, но не должен");

        itemMapper = mock(ItemMapper.class);
        service = new ItemServiceImpl(userService, this.itemRepository, itemMapper, bookingRepository,
                commentRepository, commentMapper, bookingMapper, itemRequestService);
    }

    @Test
    @DisplayName("Обновление доступности вещи")
    void updateItem_compareResult_whenAvailable() {
        itemMapper = new ItemMapper();
        service = new ItemServiceImpl(userService, itemRepository, itemMapper, bookingRepository,
                commentRepository, commentMapper, bookingMapper, itemRequestService);
        when(userService.getUser(anyLong())).thenReturn(User.builder().id(1L).build());
        Item itemRepository = Item.builder()
                .owner(User.builder().id(1L).build())
                .name("name")
                .description("Desc")
                .available(false).build();
        when(this.itemRepository.findById(anyLong())).thenReturn(Optional.of(itemRepository));
        ItemDto update = ItemDto.builder().available(true).description("description").name("Pen").build();

        ItemDto itemDto = service.update(1, 1, update);

        assertNotNull(itemDto, "Null при возвращении (проверить работу маппера)");
        assertEquals(update.getAvailable(), itemDto.getAvailable(), "не сохроняет available");
        assertEquals(itemRepository.getDescription(), itemDto.getDescription(), "Изменяет description, но не должен");
        assertEquals(itemRepository.getName(), itemDto.getName(), "Изменяет name, но не должен");

        itemMapper = mock(ItemMapper.class);
        service = new ItemServiceImpl(userService, this.itemRepository, itemMapper, bookingRepository,
                commentRepository, commentMapper, bookingMapper, itemRequestService);
    }

    @Test
    @DisplayName("Добавление комента")
    void addComment_compareResult_whenObjectCorrect() {
        when(bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(anyLong(), anyLong(),
                any(), any()))
                .thenReturn(List.of(Booking.builder().id(1L)
                        .start(LocalDateTime.now())
                        .end(LocalDateTime.now()).build()));
        User user = User.builder().name("name").build();
        LocalDateTime time = LocalDateTime.now();
        when(userService.getUser(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder().build()));
        when(commentRepository.save(any())).thenReturn(Comment.builder().id(1L)
                .created(time)
                .author(user)
                .item(Item.builder().build())
                .text("text").build());
        CommentDto commentDto = CommentDto.builder().text("text").build();

        CommentDto commentDto1 = service.addComment(1, 1, commentDto);

        assertEquals(commentDto.getText(), commentDto1.getText(), "не сохроняет текст");
        assertEquals(user.getName(), commentDto1.getAuthorName(), "не сохроняет name");
        assertEquals(time, commentDto1.getCreated(), "не сохроняет time");
    }

    @Test
    @DisplayName("Поиск вещи по слову")
    void searchText_compareResult_CorrectWork() {
        when(itemRepository.search(anyString(), any())).thenReturn(Page.empty());

        List<ItemDto> itemDtos = service.searchText(1, "text", 0, 1);

        assertEquals(0, itemDtos.size(), "не вызывается поиск по тексту");
    }

    @Test
    @DisplayName("Вывод пустого списка вещей")
    void getAllEmpty_compareResult_CorrectWork() {
        when(itemRepository.findByOwnerId(anyLong(), any())).thenReturn(Page.empty());

        List<ItemDto> itemDtos = service.getAll(1, 0, 1);

        assertEquals(0, itemDtos.size(), "не вызывается поиск по тексту");
    }

    @Test
    @DisplayName("Вывод списка вещей")
    void ItemGetById_compareResult_whenObjectCorrect() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder()
                .owner(User.builder().id(1L).build()).build()));
        when(itemMapper.toItemDto(any())).thenReturn(ItemDto.builder().id(1L)
                .owner(User.builder().id(1L).build()).build());
        when(bookingRepository.findFirst1ByItemIdAndStartBeforeOrderByStartDesc(anyLong(), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(anyLong(), any(), any()))
                .thenReturn(Optional.empty());
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of());

        ItemDto itemDtos = service.getById(1, 1);

        assertNull(itemDtos.getNextBooking(), "Booking не присваивается");
        assertNull(itemDtos.getLastBooking(), "Booking не присваивается");
        assertEquals(0, itemDtos.getComments().size(), "комментарии не присваиваются");
    }

    @Test
    @DisplayName("Вывод списка вещей при бронировании")
    void getAllItem_compareResult_whenManyBooking() {
        itemMapper = new ItemMapper();
        service = new ItemServiceImpl(userService, itemRepository, itemMapper, bookingRepository,
                commentRepository, commentMapper, bookingMapper, itemRequestService);
        Item item = Item.builder().id(1L).name("first").description("desc").build();
        Item item1 = Item.builder().id(2L).name("second").description("desc1").build();
        Item item2 = Item.builder().id(3L).name("free").description("desc2").build();
        when(itemRepository.findByOwnerId(anyLong(), any())).thenReturn(new PageImpl<>(List.of(item2, item, item1)));

        Booking bookingLast = Booking.builder().id(1L).end(LocalDateTime.now().minusDays(2)).start(LocalDateTime.now().minusDays(3))
                .item(item2).booker(User.builder().id(1L).build()).build();
        Booking bookingLast1 = Booking.builder().id(2L).end(LocalDateTime.now().minusHours(1)).start(LocalDateTime.now().minusDays(1))
                .item(item2).booker(User.builder().id(1L).build()).build();
        Booking bookingLast2 = Booking.builder().id(3L).end(LocalDateTime.now().minusMonths(1)).start(LocalDateTime.now().minusDays(5))
                .item(item2).booker(User.builder().id(1L).build()).build();
        when(bookingRepository.findByItemInAndStartBeforeOrderByStartDesc(any(), any()))
                .thenReturn(List.of(bookingLast1, bookingLast, bookingLast2));

        Booking bookingNext = Booking.builder().id(1L).end(LocalDateTime.now().plusDays(2)).start(LocalDateTime.now().plusDays(3))
                .item(item2).booker(User.builder().id(1L).build()).build();
        Booking bookingNext1 = Booking.builder().id(3L).end(LocalDateTime.now().plusHours(1)).start(LocalDateTime.now().plusDays(1))
                .item(item2).booker(User.builder().id(1L).build()).build();
        Booking bookingNext2 = Booking.builder().id(2L).end(LocalDateTime.now().plusMonths(1)).start(LocalDateTime.now().plusDays(5))
                .item(item2).booker(User.builder().id(1L).build()).build();
        when(bookingRepository.findByItemInAndStartAfterAndStatusNotLikeOrderByStartAsc(any(), any(), any()))
                .thenReturn(List.of(bookingNext1, bookingNext, bookingNext2));

        when(commentRepository.findByItemIn(any(), any())).thenReturn(List.of(Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(User.builder().name("Name").build())
                .build()));

        List<ItemDto> itemDtos = service.getAll(1, 0, 5);

        BookingItemDto bookingDtoLast = BookingItemDto.builder().bookerId(1L).id(2L).build();
        BookingItemDto bookingDtoNext = BookingItemDto.builder().bookerId(1L).id(3L).build();
        CommentDto commentDto = CommentDto.builder().id(1L).text("text").authorName("Name").build();

        assertEquals(bookingDtoLast, itemDtos.get(0).getLastBooking(), "Не сохроняет нужное последние бронирование");
        assertEquals(bookingDtoNext, itemDtos.get(0).getNextBooking(), "Не сохроняет нужное следующее бронирование");
        assertNull(itemDtos.get(1).getNextBooking(), "Сохроняет когда должен быть null");
        assertNull(itemDtos.get(1).getLastBooking(), "Сохроняет когда должен быть null");
        assertEquals(commentDto, itemDtos.get(1).getComments().get(0), "Сохроняет когда должен быть null");
        assertNull(itemDtos.get(2).getNextBooking(), "Сохроняет когда должен быть null");
        assertNull(itemDtos.get(2).getLastBooking(), "Сохроняет когда должен быть null");

        itemMapper = mock(ItemMapper.class);
        service = new ItemServiceImpl(userService, itemRepository, itemMapper, bookingRepository,
                commentRepository, commentMapper, bookingMapper, itemRequestService);
    }
}