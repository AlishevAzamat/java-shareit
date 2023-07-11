package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest {
    private final UserRepository userRepository = new UserRepositoryImpl();

    @BeforeEach
    void beforeEach() {
        userRepository.getAll().clear();
    }

    @Test
    void add() {
        User user = userRepository.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, user.getId());
        assertEquals(1, userRepository.getAll().size());
    }

    @Test
    void update() {
        User user = userRepository.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());
        user.setName("Nik");
        User update = userRepository.update(user);

        assertEquals(1, update.getId());
        assertEquals("Nik", update.getName());
        assertEquals(1, userRepository.getAll().size());
    }

    @Test
    void delete() {
        User user = userRepository.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());
        userRepository.delete(user.getId());

        assertEquals(0, userRepository.getAll().size());
    }

    @Test
    void getAll() {
        assertEquals(0, userRepository.getAll().size());

        userRepository.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, userRepository.getAll().size());

        userRepository.delete(1);

        assertEquals(0, userRepository.getAll().size());
    }
}