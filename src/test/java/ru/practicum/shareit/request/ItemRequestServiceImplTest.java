package ru.practicum.shareit.request;

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

    private final ItemRequestRepository repository = mock(ItemRequestRepository.class);

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private final ItemRequestMapper mapper = new ItemRequestMapper();

    private final ItemMapper itemMapper = new ItemMapper();

    private final ItemRequestService service =
            new ItemRequestServiceImpl(userService, repository, itemRepository, mapper, itemMapper);

    @Test
    void getAllByUserFromNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByUser(1, -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserSizeNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByUser(1, 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserSizeZero() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllByUser(1, 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllFromNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAll(1, -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllSizeNegative() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAll(1, 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllSizeZero() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.getAll(1, 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void replyNotFoundException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            service.reply(0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void replyIncorrectCountException() {
        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            service.reply(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void replyGetRequest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .description("desc")
                .id(1L)
                .created(LocalDateTime.now())
                .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequest request = service.reply(1);

        assertEquals(itemRequest.getId(), request.getId(), "Не возвращает id");
        assertEquals(itemRequest.getCreated(), request.getCreated(), "Не возвращает created");
        assertEquals(itemRequest.getDescription(), request.getDescription(), "Не возвращает description");
    }

    @Test
    void getByIdWithItemsEmptyAndMapper() {
        when(userService.getById(anyLong()))
                .thenReturn(User.builder().id(1L).email("user@mail").name("name").build());
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("desc")
                .build();
        when(repository.findById(anyLong()))
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
    void getByIdWithItemsAndMapper() {
        when(userService.getById(anyLong()))
                .thenReturn(User.builder().id(1L).email("user@mail").name("name").build());
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("desc")
                .build();
        when(repository.findById(anyLong()))
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
    void getAllEmpty() {
        when(userService.getById(anyLong()))
                .thenReturn(User.builder().id(1L).email("user@mail").name("name").build());
        when(repository.findByOwnerIdNot(anyLong(), any()))
                .thenReturn(Page.empty());

        List<ItemRequestDto> requests = service.getAll(1, 0, 1);

        assertNotNull(requests, "null не возвращает список");
        assertEquals(0, requests.size(), "не пустой список");
    }

    @Test
    void getAllByUserEmpty() {
        when(userService.getById(anyLong()))
                .thenReturn(User.builder().id(1L).email("user@mail").name("name").build());
        when(repository.findByOwnerId(anyLong(), any()))
                .thenReturn(Page.empty());

        List<ItemRequestDto> requests = service.getAllByUser(1, 0, 1);

        assertNotNull(requests, "null не возвращает список");
        assertEquals(0, requests.size(), "не пустой список");
    }

    @Test
    void getAllByUser() {
        LocalDateTime time = LocalDateTime.now();
        ItemRequestDto requestDto = ItemRequestDto.builder().id(1L).description("desc")
                .created(time).items(List.of()).build();
        when(userService.getById(anyLong()))
                .thenReturn(User.builder().id(1L).email("user@mail").name("name").build());
        when(repository.findByOwnerId(anyLong(), any()))
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