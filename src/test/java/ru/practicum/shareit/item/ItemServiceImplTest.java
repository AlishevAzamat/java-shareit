package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.ParameterNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceImplTest {
    private final ItemRepository itemRepository = new ItemRepositoryImpl();
    private final UserRepository userRepository = new UserRepositoryImpl();
    private final UserService userService = new UserServiceImpl(userRepository);
    private final ItemService itemService = new ItemServiceImpl(userService, itemRepository);
    private final ItemMapper itemMapper = new ItemMapper();

    @BeforeEach
    void beforeEach() {
        itemRepository.getAll().clear();
        userRepository.getAll().clear();
        userService.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());
    }

    @Test
    void add() {
        Item item = itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(1, item.getId());
        assertEquals(1, itemRepository.getAll().size());
    }

    @Test
    void addUserIdNegative() {
        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            itemService.add(-1, Item.builder().build());
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void addUserIdUnknown() {
        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            itemService.add(999, Item.builder().build());
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateAll() {
        Item item = itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        item.setAvailable(false);
        item.setDescription("description");
        item.setName("Nik");
        Item update = itemService.update(1, item.getId(), itemMapper.toItemDto(item));

        assertEquals(item.getId(), update.getId());
        assertEquals("Nik", update.getName());
        assertEquals("description", update.getDescription());
        assertFalse(update.isAvailable());
    }

    @Test
    void updateName() {
        Item item = itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        item.setName("Nik");
        Item update = itemService.update(1, item.getId(), itemMapper.toItemDto(item));

        assertEquals(item.getId(), update.getId());
        assertEquals("Nik", update.getName());
    }

    @Test
    void updateDescription() {
        Item item = itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        item.setDescription("description");
        Item update = itemService.update(1, item.getId(), itemMapper.toItemDto(item));

        assertEquals(item.getId(), update.getId());
        assertEquals("description", update.getDescription());
    }

    @Test
    void updateAvailable() {
        Item item = itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        item.setAvailable(false);
        Item update = itemService.update(1, item.getId(), itemMapper.toItemDto(item));

        assertEquals(item.getId(), update.getId());
        assertFalse(update.isAvailable());
    }

    @Test
    void updateUserIsNotOwner() {
        Item item = itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        User user = userService.add(User.builder()
                .name("name")
                .email("mail@mail.ru").build());
        item.setAvailable(false);
        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            itemService.update(user.getId(), item.getId(), itemMapper.toItemDto(item));
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getById() {
        Item item = itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(item, itemService.getById(item.getId()));
    }

    @Test
    void getByIdNegative() {
        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            itemService.getById(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getByIdUnknown() {
        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            itemService.getById(999);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAll() {
        itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());
        itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(2, itemService.getAll(1).size());
    }

    @Test
    void searchText() {
        itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(1, itemService.searchText(1, "desc").size());
    }

    @Test
    void searchUpperText() {
        itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(1, itemService.searchText(1, "DESC").size());
    }

    @Test
    void searchTextIsEmpty() {
        itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(0, itemService.searchText(1, "").size());
    }

    @Test
    void searchHalfUpperText() {
        itemService.add(1, Item.builder()
                .name("name")
                .description("desc")
                .available(true).build());

        assertEquals(1, itemService.searchText(1, "dESc").size());
    }
}