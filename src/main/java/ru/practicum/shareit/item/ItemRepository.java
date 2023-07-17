package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {

    Item add(Item item);

    Item update(Item item);

    void delete(long id);

    Item getById(long id);

    List<Item> getAll();

    List<Item> getAllByOwnerId(long id);

    List<Item> findByText(long userId, String str);
}
