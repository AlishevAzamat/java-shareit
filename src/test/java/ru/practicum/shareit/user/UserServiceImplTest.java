package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.DuplicateException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    private final UserRepository repository = new UserRepositoryImpl();
    private final UserMapper userMapper = new UserMapper();
    private final UserService service = new UserServiceImpl(repository, userMapper);

    @BeforeEach
    void beforeEach() {
        service.getAll().clear();
    }

    @Test
    void add() {
        UserDto user = service.add(UserDto.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, user.getId());
        assertEquals(1, repository.getAll().size());
    }

    @Test
    void addDuplicateEmail() {
        service.add(UserDto.builder()
                .name("name")
                .email("email@mail.ru").build());

        Throwable thrown = assertThrows(DuplicateException.class, () -> {
            service.add(UserDto.builder()
                    .name("name")
                    .email("email@mail.ru").build());
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateAll() {
        UserDto user = service.add(UserDto.builder()
                .name("name")
                .email("email@mail.ru").build());
        user.setName("Nik");
        user.setEmail("user@user.ru");
        UserDto update = service.update(user.getId(), user);

        assertEquals(user.getId(), update.getId());
        assertEquals("Nik", update.getName());
        assertEquals("user@user.ru", update.getEmail());
    }

    @Test
    void updateName() {
        UserDto user = service.add(UserDto.builder()
                .name("name")
                .email("email@mail.ru").build());
        user.setName("Nik");
        UserDto update = service.update(user.getId(), user);

        assertEquals(user.getId(), update.getId());
        assertEquals("Nik", update.getName());
        assertEquals("email@mail.ru", update.getEmail());
    }

    @Test
    void updateDuplicateEmail() {
        service.add(UserDto.builder()
                .name("name")
                .email("email@mail.ru").build());

        UserDto user = service.add(UserDto.builder()
                .name("name")
                .email("user@mail.ru").build());
        user.setEmail("email@mail.ru");
        Throwable thrown = assertThrows(DuplicateException.class, () -> {
            service.update(user.getId(), user);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateEmail() {
        UserDto user = service.add(UserDto.builder()
                .name("name")
                .email("email@mail.ru").build());
        user.setEmail("user@user.ru");
        UserDto update = service.update(user.getId(), user);

        assertEquals(user.getId(), update.getId());
        assertEquals("name", update.getName());
        assertEquals("user@user.ru", update.getEmail());
    }

    @Test
    void getById() {
        UserDto user = service.add(UserDto.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(user, userMapper.toUserDto(service.getById(user.getId())));
    }

    @Test
    void getAll() {
        assertEquals(0, service.getAll().size());

        UserDto user = service.add(UserDto.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, service.getAll().size());

        service.delete(user.getId());

        assertEquals(0, service.getAll().size());
    }

    @Test
    void delete() {
        UserDto user = service.add(UserDto.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, repository.getAll().size());

        service.delete(user.getId());

        assertEquals(0, repository.getAll().size());
    }
}