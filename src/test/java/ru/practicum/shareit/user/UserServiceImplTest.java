package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.ParameterNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    private final UserRepository userRepository = new UserRepositoryImpl();
    private final UserService userService = new UserServiceImpl(userRepository);
    private final UserMapper userMapper = new UserMapper();

    @BeforeEach
    void beforeEach() {
        userService.getAll().clear();
    }

    @Test
    void add() {
        User user = userService.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, user.getId());
        assertEquals(1, userRepository.getAll().size());
    }

    @Test
    void addDuplicateEmail() {
        userService.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        Throwable thrown = assertThrows(DuplicateException.class, () -> {
            userService.add(User.builder()
                    .name("name")
                    .email("email@mail.ru").build());
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateAll() {
        User user = userService.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());
        user.setName("Nik");
        user.setEmail("user@user.ru");
        User update = userService.update(user.getId(), userMapper.toUserDto(user));

        assertEquals(user.getId(), update.getId());
        assertEquals("Nik", update.getName());
        assertEquals("user@user.ru", update.getEmail());
    }

    @Test
    void updateName() {
        User user = userService.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());
        user.setName("Nik");
        User update = userService.update(user.getId(), userMapper.toUserDto(user));

        assertEquals(user.getId(), update.getId());
        assertEquals("Nik", update.getName());
        assertEquals("email@mail.ru", update.getEmail());
    }

    @Test
    void updateDuplicateEmail() {
        userService.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        User user = userService.add(User.builder()
                .name("name")
                .email("user@mail.ru").build());
        user.setEmail("email@mail.ru");
        Throwable thrown = assertThrows(DuplicateException.class, () -> {
            userService.update(user.getId(), userMapper.toUserDto(user));
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateEmail() {
        User user = userService.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());
        user.setEmail("user@user.ru");
        User update = userService.update(user.getId(), userMapper.toUserDto(user));

        assertEquals(user.getId(), update.getId());
        assertEquals("name", update.getName());
        assertEquals("user@user.ru", update.getEmail());
    }

    @Test
    void getById() {
        User user = userService.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(user, userService.getById(user.getId()));
    }

    @Test
    void getByIdNegative() {
        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            userService.getById(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getByIdUnknown() {
        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            userService.getById(999);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAll() {
        assertEquals(0, userService.getAll().size());

        User user = userService.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, userService.getAll().size());

        userService.delete(user.getId());

        assertEquals(0, userService.getAll().size());
    }

    @Test
    void delete() {
        User user = userService.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, userRepository.getAll().size());

        userService.delete(user.getId());

        assertEquals(0, userRepository.getAll().size());
    }

    @Test
    void deleteByIdNegative() {
        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            userService.delete(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void deleteByIdUnknown() {
        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            userService.delete(999);
        });

        assertNotNull(thrown.getMessage());
    }
}