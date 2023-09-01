package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {
    private final UserMapper userMapper = new UserMapper();

    @Test
    @DisplayName("Маппер toUserDto")
    void user_compareResult_toUserDto() {
        User user = User.builder().id(1L).name("name").email("user@mail").build();
        UserDto userDto = userMapper.toUserDto(user);

        assertEquals(user.getId(), userDto.getId(), "не сохроняет id в dto");
        assertEquals(user.getName(), userDto.getName(), "не сохроняет name в dto");
        assertEquals(user.getEmail(), userDto.getEmail(), "не сохроняет email в dto");
    }

    @Test
    @DisplayName("Маппер toUser")
    void user_compareResult_toUser() {
        UserDto userDto = UserDto.builder().name("name").email("user@mail").build();
        User user = userMapper.toUser(userDto);

        assertNull(user.getId(), "id не null в model");
        assertEquals(userDto.getName(), user.getName(), "не сохроняет name в model");
        assertEquals(userDto.getEmail(), user.getEmail(), "не сохроняет email в model");
    }
}