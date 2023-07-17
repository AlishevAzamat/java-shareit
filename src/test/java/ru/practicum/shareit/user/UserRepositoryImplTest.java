package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRepositoryImplTest {
    private final UserRepository repository = new UserRepositoryImpl();

    @BeforeEach
    void beforeEach() {
        repository.getAll().clear();
    }

    @Test
    void add() {
        User user = repository.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, user.getId());
        assertEquals(1, repository.getAll().size());
    }

    @Test
    void update() {
        User user = repository.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());
        user.setName("Nik");
        User update = repository.update(user);

        assertEquals(1, update.getId());
        assertEquals("Nik", update.getName());
        assertEquals(1, repository.getAll().size());
    }

    @Test
    void delete() {
        User user = repository.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());
        repository.delete(user.getId());

        assertEquals(0, repository.getAll().size());
    }

    @Test
    void getAll() {
        assertEquals(0, repository.getAll().size());

        repository.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, repository.getAll().size());

        repository.delete(1);

        assertEquals(0, repository.getAll().size());
    }
}