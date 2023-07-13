package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ParameterNotFoundException;
import ru.practicum.shareit.user.*;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceImplTest {
    private final ItemRepository itemRepository = new ItemRepositoryImpl();
    private final UserRepository userRepository = new UserRepositoryImpl();
    private final ItemMapper itemMapper = new ItemMapper();
    private final UserMapper userMapper = new UserMapper();
    private final UserService userService = new UserServiceImpl(userRepository, userMapper);
    private final ItemService itemService = new ItemServiceImpl(userService, itemRepository, itemMapper);

    @BeforeEach
    void beforeEach() {
        itemRepository.getAll().clear();
        userRepository.getAll().clear();
        userService.add(UserDto.builder()
                .name("name")
                .email("email@mail.ru").build());
    }

    @Test
    void add() {
        ItemDto item = itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(1, item.getId());
        assertEquals(1, itemRepository.getAll().size());
    }

    @Test
    void addUserIdUnknown() {
        Throwable thrown = assertThrows(NullPointerException.class, () -> {
            itemService.add(999, ItemDto.builder().build());
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateAll() {
        ItemDto item = itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        item.setAvailable(false);
        item.setDescription("description");
        item.setName("Nik");
        ItemDto update = itemService.update(1, item.getId(), item);

        assertEquals(item.getId(), update.getId());
        assertEquals("Nik", update.getName());
        assertEquals("description", update.getDescription());
        assertFalse(update.getAvailable());
    }

    @Test
    void updateName() {
        ItemDto item = itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        item.setName("Nik");
        ItemDto update = itemService.update(1, item.getId(), item);

        assertEquals(item.getId(), update.getId());
        assertEquals("Nik", update.getName());
    }

    @Test
    void updateDescription() {
        ItemDto item = itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        item.setDescription("description");
        ItemDto update = itemService.update(1, item.getId(), item);

        assertEquals(item.getId(), update.getId());
        assertEquals("description", update.getDescription());
    }

    @Test
    void updateAvailable() {
        ItemDto item = itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        item.setAvailable(false);
        ItemDto update = itemService.update(1, item.getId(), item);

        assertEquals(item.getId(), update.getId());
        assertFalse(update.getAvailable());
    }

    @Test
    void updateUserIsNotOwner() {
        ItemDto item = itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        UserDto user = userService.add(UserDto.builder()
                .name("name")
                .email("mail@mail.ru").build());
        item.setAvailable(false);
        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            itemService.update(user.getId(), item.getId(), item);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getById() {
        ItemDto item = itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(item, itemService.getById(item.getId()));
    }

    @Test
    void getByIdUnknown() {
        Throwable thrown = assertThrows(NullPointerException.class, () -> {
            itemService.getById(999);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAll() {
        itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(2, itemService.getAll(1).size());
    }

    @Test
    void searchText() {
        itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(1, itemService.searchText(1, "desc").size());
    }

    @Test
    void searchUpperText() {
        itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(1, itemService.searchText(1, "DESC").size());
    }

    @Test
    void searchTextIsEmpty() {
        itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(0, itemService.searchText(1, "").size());
    }

    @Test
    void searchHalfUpperText() {
        itemService.add(1, ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(1, itemService.searchText(1, "dESc").size());
    }
}