package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.ParameterNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class UserServiceImplTest {
    private final UserRepository repository = mock(UserRepository.class);

    private final UserMapper mapper = new UserMapper();

    private final UserService service = new UserServiceImpl(repository, mapper);

    @Test
    void getUserNegative() {
        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            service.getUser(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getUserUnknown() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            service.getUser(0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getUser() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(userRepository));

        User user = service.getUser(1);

        assertNotNull(user, "Null при получении model");
        assertEquals(userRepository, user, "Не передаёт объект");
    }

    @Test
    void getUserByIdWithMapper() {
        User userCurrent = User.builder()
                .id(1L)
                .email("user@mail")
                .name("name").build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build()));

        User user = service.getById(1);

        assertNotNull(user, "Null при получении dto");
        assertEquals(userCurrent, user, "Не передаёт объект");
    }

    @Test
    void updateNameAndEmailWithMapper() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(userRepository));
        User userUpdate = User.builder()
                .id(1L)
                .name("Nik")
                .email("Nikol@mail")
                .build();
        when(repository.save(any())).thenReturn(userUpdate);
        UserDto update = UserDto.builder()
                .name("Nik")
                .email("Nikol@mail")
                .build();

        UserDto userDto = service.update(1L, update);

        assertNotNull(userDto, "null при получении (посмотреть маппер)");
        assertEquals(update.getName(), userDto.getName(), "Не изменяется имя");
        assertEquals(update.getEmail(), userDto.getEmail(), "Не изменяется почта");
    }

    @Test
    void updateNameWithMapper() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(userRepository));
        when(repository.save(any())).thenReturn(User.builder()
                .id(1L)
                .name("Nik")
                .email("user@mail")
                .build());
        UserDto update = UserDto.builder()
                .name("Nik")
                .build();

        UserDto userDto = service.update(1L, update);

        assertNotNull(userDto, "null при получении (посмотреть маппер)");
        assertEquals(update.getName(), userDto.getName(), "Не изменяется имя");
        assertEquals(userRepository.getEmail(), userDto.getEmail(), "Изменяется почта, хотя не должна");
    }

    @Test
    void updateEmailWithMapper() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(userRepository));
        when(repository.save(any())).thenReturn(User.builder()
                .id(1L)
                .name("name")
                .email("Nikol@mail")
                .build());
        UserDto update = UserDto.builder()
                .email("Nikol@mail")
                .build();

        UserDto userDto = service.update(1L, update);

        assertNotNull(userDto, "null при получении (посмотреть маппер)");
        assertEquals(userRepository.getName(), userDto.getName(), "Изменяется имя, хотя не должно");
        assertEquals(update.getEmail(), userDto.getEmail(), "Не изменяется почта");
    }

    @Test
    void getAllEmpty() {
        when(repository.findAll()).thenReturn(List.of());
        List<UserDto> users = service.getAll();

        assertNotNull(users, "null при получении");
        assertEquals(0, users.size(), "Не пустой список при не добовлении");
    }

    @Test
    void getAllWithMapper() {
        UserDto user = UserDto.builder().id(1L).email("user@mail").name("name").build();
        when(repository.findAll()).thenReturn(List.of(User.builder().id(1L).email("user@mail").name("name").build()));
        List<UserDto> users = service.getAll();

        assertNotNull(users, "null при получении");
        assertEquals(user, users.get(0), "Не пустой список при не добовлении");
    }

    @Test
    void addNewUserWithMapper() {
        UserDto userDto = UserDto.builder().name("name").email("user@mail").build();
        when(repository.save(any())).thenReturn(User.builder().id(1L).name("name").email("user@mail").build());
        UserDto userDtoNew = service.add(userDto);

        assertNotNull(userDtoNew, "null при получении");
        assertEquals(1, userDtoNew.getId(), "Не возвращает при добавлении id");
        assertEquals(userDto.getName(), userDtoNew.getName(), "Не возвращает при добавлении name");
        assertEquals(userDto.getEmail(), userDtoNew.getEmail(), "Не возвращает при добавлении email");
    }

    @Test
    void deleteUser() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        service.delete(1);
    }
}