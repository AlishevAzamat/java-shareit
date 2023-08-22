package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.ParameterNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {
    private final UserService userService = mock(UserService.class);

    private final ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    private final ItemMapper itemMapper = new ItemMapper();

    private final ItemRequestService service =
            new ItemRequestServiceImpl(userService, itemRequestRepository, itemRepository, itemRequestMapper, itemMapper);

    @Test
    @DisplayName("Вывод всех пользователей from -1")
    void getAllByUser_throwIllegalArgumentException_whenFromNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByUser(1, -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Вывод всех пользователей size -1")
    void getAllByUser_throwIllegalArgumentException_whenSizeNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByUser(1, 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Вывод всех пользователей size 0")
    void getAllByUser_throwIllegalArgumentException_whenSizeZero() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByUser(1, 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Вывод запросов from -1")
    void getAllItem_throwIllegalArgumentException_whenFromNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAll(1, -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Вывод запросов size -1")
    void getAllItem_throwIllegalArgumentException_whenSizeNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAll(1, 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Вывод запросов size 0")
    void getAllItem_throwIllegalArgumentException_whenSizeZero() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAll(1, 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Запрос не найдена")
    void itemReply_throwParameterNotFoundException_NotFoundException() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            service.reply(0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Запрос не найдена -1")
    void itemReply_throwIncorrectParameterException_IncorrectCountException() {
        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            service.reply(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Проверка запроса")
    void replyGetRequest_compareResult_whenObjectCorrect() {
        ItemRequest itemRequest = ItemRequest.builder()
                .description("desc")
                .id(1L)
                .created(LocalDateTime.now())
                .build();
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequest request = service.reply(1);

        assertEquals(itemRequest.getId(), request.getId(), "Не возвращает id");
        assertEquals(itemRequest.getCreated(), request.getCreated(), "Не возвращает created");
        assertEquals(itemRequest.getDescription(), request.getDescription(), "Не возвращает description");
    }

    @Test
    @DisplayName("Вывод запроса по айди")
    void getByIdIteM_compareResult_whenItemsEmptyAndMapper() {
        when(userService.getById(anyLong()))
                .thenReturn(User.builder().id(1L).email("user@mail").name("name").build());
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("desc")
                .build();
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.findByRequestInOrderByIdAsc(any()))
                .thenReturn(List.of());

        ItemRequestDto requestDto = service.getById(1, 1);

        assertNotNull(requestDto, "null не возвращает dto");
        assertEquals(request.getDescription(), requestDto.getDescription(), "не возвращает desc");
        assertEquals(request.getId(), requestDto.getId(), "не возвращает id");
        assertEquals(0, requestDto.getItems().size(), "не возвращает пустой список items");
    }

    @Test
    @DisplayName("Вывод запроса по айди Успешно")
    void getByIdItem_compareResult_whenItemsAndMapper() {
        when(userService.getById(anyLong()))
                .thenReturn(User.builder().id(1L).email("user@mail").name("name").build());
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("desc")
                .build();
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.findByRequestInOrderByIdAsc(any()))
                .thenReturn(List.of(Item.builder().id(1L).build()));

        ItemRequestDto requestDto = service.getById(1, 1);

        assertNotNull(requestDto, "null не возвращает dto");
        assertEquals(request.getDescription(), requestDto.getDescription(), "не возвращает desc");
        assertEquals(request.getId(), requestDto.getId(), "не возвращает id");
        assertEquals(1, requestDto.getItems().size(), "не возвращает список items с 1");
    }

    @Test
    @DisplayName("Вывод пустого списка")
    void getAllItem_compareResult_whenEmpty() {
        when(userService.getById(anyLong()))
                .thenReturn(User.builder().id(1L).email("user@mail").name("name").build());
        when(itemRequestRepository.findByOwnerIdNot(anyLong(), any()))
                .thenReturn(Page.empty());

        List<ItemRequestDto> requests = service.getAll(1, 0, 1);

        assertNotNull(requests, "null не возвращает список");
        assertEquals(0, requests.size(), "не пустой список");
    }

    @Test
    @DisplayName("Вывод запроса когда пользователь пустой")
    void getAllItems_compareResult_whenUserEmpty() {
        when(userService.getById(anyLong()))
                .thenReturn(User.builder().id(1L).email("user@mail").name("name").build());
        when(itemRequestRepository.findByOwnerId(anyLong(), any()))
                .thenReturn(Page.empty());

        List<ItemRequestDto> requests = service.getAllByUser(1, 0, 1);

        assertNotNull(requests, "null не возвращает список");
        assertEquals(0, requests.size(), "не пустой список");
    }

    @Test
    @DisplayName("Вывод запроса")
    void getAllItems_compareResult_whenObjectCorrect() {
        LocalDateTime time = LocalDateTime.now();
        ItemRequestDto requestDto = ItemRequestDto.builder().id(1L).description("desc")
                .created(time).items(List.of()).build();
        when(userService.getById(anyLong()))
                .thenReturn(User.builder().id(1L).email("user@mail").name("name").build());
        when(itemRequestRepository.findByOwnerId(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(ItemRequest.builder().id(1L).description("desc")
                        .created(time).build())));
        when(itemRepository.findByRequestInOrderByIdAsc(any())).thenReturn(List.of());

        List<ItemRequestDto> requests = service.getAllByUser(1, 0, 1);

        assertNotNull(requests, "null не возвращает список");
        assertEquals(1, requests.size(), "Пустой список");
        assertEquals(requestDto, requests.get(0), "Не тот объект сохраняется");
        assertEquals(0, requests.get(0).getItems().size(), "не пустой список items");
    }

}