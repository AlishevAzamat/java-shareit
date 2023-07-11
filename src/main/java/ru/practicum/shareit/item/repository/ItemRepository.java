package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item add(Item item);

    Item update(Item item);

    void delete(long id);

    Item getById(long id);

    List<Item> getAll();
}
