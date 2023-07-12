package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User add(UserDto userDto);

    User update(Long id, UserDto userDto);

    void delete(long id);

    User getById(long id);

    List<User> getAll();
}
