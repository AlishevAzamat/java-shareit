package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
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
    private final UserRepository userRepository = mock(UserRepository.class);

    private final UserMapper userMapper = new UserMapper();

    private final UserService userService = new UserServiceImpl(userRepository, userMapper);

    @Test
    @DisplayName("Передается неверный параметр")
    void getUser_throwIncorrectParameterException_whenNegative() {
        Throwable thrown = assertThrows(IncorrectParameterException.class, () -> {
            userService.getUser(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Вывод неизвестного пользователя")
    void getUser_throwParameterNotFoundException_whenUserUnknown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(ParameterNotFoundException.class, () -> {
            userService.getUser(0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    @DisplayName("Вывод пользователя")
    void getUser_compareResult_whenObjectCorrect() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build();
        when(this.userRepository.findById(anyLong())).thenReturn(Optional.of(userRepository));

        User user = userService.getUser(1);

        assertNotNull(user, "Null при получении model");
        assertEquals(userRepository, user, "Не передаёт объект");
    }

    @Test
    @DisplayName("Вывод пользователя 1")
    void getUserById_compareResult_whenObjectCorrect() {
        User userCurrent = User.builder()
                .id(1L)
                .email("user@mail")
                .name("name").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build()));

        User user = userService.getById(1);

        assertNotNull(user, "Null при получении dto");
        assertEquals(userCurrent, user, "Не передаёт объект");
    }

    @Test
    @DisplayName("Обновление пользователя")
    void updateUser_compareResult_whenObjectCorrect() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build();
        when(this.userRepository.findById(anyLong())).thenReturn(Optional.of(userRepository));
        User userUpdate = User.builder()
                .id(1L)
                .name("Nik")
                .email("Nikol@mail")
                .build();
        when(this.userRepository.save(any())).thenReturn(userUpdate);
        UserDto update = UserDto.builder()
                .name("Nik")
                .email("Nikol@mail")
                .build();

        UserDto userDto = userService.update(1L, update);

        assertNotNull(userDto, "null при получении (посмотреть маппер)");
        assertEquals(update.getName(), userDto.getName(), "Не изменяется имя");
        assertEquals(update.getEmail(), userDto.getEmail(), "Не изменяется почта");
    }

    @Test
    @DisplayName("Обновление имени пользователя")
    void updateUserName_compareResult_whenObjectCorrect() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build();
        when(this.userRepository.findById(anyLong())).thenReturn(Optional.of(userRepository));
        when(this.userRepository.save(any())).thenReturn(User.builder()
                .id(1L)
                .name("Nik")
                .email("user@mail")
                .build());
        UserDto update = UserDto.builder()
                .name("Nik")
                .build();

        UserDto userDto = userService.update(1L, update);

        assertNotNull(userDto, "null при получении (посмотреть маппер)");
        assertEquals(update.getName(), userDto.getName(), "Не изменяется имя");
        assertEquals(userRepository.getEmail(), userDto.getEmail(), "Изменяется почта, хотя не должна");
    }

    @Test
    @DisplayName("Обновление почты пользователя")
    void updateUserEmail_compareResult_whenObjectCorrect() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build();
        when(this.userRepository.findById(anyLong())).thenReturn(Optional.of(userRepository));
        when(this.userRepository.save(any())).thenReturn(User.builder()
                .id(1L)
                .name("name")
                .email("Nikol@mail")
                .build());
        UserDto update = UserDto.builder()
                .email("Nikol@mail")
                .build();

        UserDto userDto = userService.update(1L, update);

        assertNotNull(userDto, "null при получении (посмотреть маппер)");
        assertEquals(userRepository.getName(), userDto.getName(), "Изменяется имя, хотя не должно");
        assertEquals(update.getEmail(), userDto.getEmail(), "Не изменяется почта");
    }

    @Test
    @DisplayName("Вывод пустого списка")
    void getAllUsers_compareResult_whenEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());
        List<UserDto> users = userService.getAll();

        assertNotNull(users, "null при получении");
        assertEquals(0, users.size(), "Не пустой список при не добовлении");
    }

    @Test
    @DisplayName("Вывод списка пользователей")
    void getAllUsers_compareResult_whenObjectCorrect() {
        UserDto user = UserDto.builder().id(1L).email("user@mail").name("name").build();
        when(userRepository.findAll()).thenReturn(List.of(User.builder().id(1L).email("user@mail").name("name").build()));
        List<UserDto> users = userService.getAll();

        assertNotNull(users, "null при получении");
        assertEquals(user, users.get(0), "Не пустой список при не добовлении");
    }

    @Test
    @DisplayName("Добавление пользователя")
    void addUser_compareResult_whenObjectCorrect() {
        UserDto userDto = UserDto.builder().name("name").email("user@mail").build();
        when(userRepository.save(any())).thenReturn(User.builder().id(1L).name("name").email("user@mail").build());
        UserDto userDtoNew = userService.add(userDto);

        assertNotNull(userDtoNew, "null при получении");
        assertEquals(1, userDtoNew.getId(), "Не возвращает при добавлении id");
        assertEquals(userDto.getName(), userDtoNew.getName(), "Не возвращает при добавлении name");
        assertEquals(userDto.getEmail(), userDtoNew.getEmail(), "Не возвращает при добавлении email");
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser_compareResult_whenObjectCorrect() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        userService.delete(1);
    }
}