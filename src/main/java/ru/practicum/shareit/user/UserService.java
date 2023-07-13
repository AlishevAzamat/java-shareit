package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    void delete(long id);

    User getById(long id);

    List<UserDto> getAll();
}
