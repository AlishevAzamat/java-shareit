package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {
    private final UserMapper mapper = new UserMapper();

    @Test
    void toUserDto() {
        User user = User.builder().id(1L).name("name").email("user@mail").build();
        UserDto userDto = mapper.toUserDto(user);

        assertEquals(user.getId(), userDto.getId(), "не сохроняет id в dto");
        assertEquals(user.getName(), userDto.getName(), "не сохроняет name в dto");
        assertEquals(user.getEmail(), userDto.getEmail(), "не сохроняет email в dto");
    }

    @Test
    void toUser() {
        UserDto userDto = UserDto.builder().name("name").email("user@mail").build();
        User user = mapper.toUser(userDto);

        assertNull(user.getId(), "id не null в model");
        assertEquals(userDto.getName(), user.getName(), "не сохроняет name в model");
        assertEquals(userDto.getEmail(), user.getEmail(), "не сохроняет email в model");
    }
}