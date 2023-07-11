package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User add(User user);

    User update(Long id, UserDto userDto);

    void delete(long id);

    User getById(long id);

    List<User> getAll();
}
