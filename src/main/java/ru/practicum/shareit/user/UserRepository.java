package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User add(User user);

    User update(User user);

    void delete(long id);

    User getById(long id);

    List<User> getAll();
}
