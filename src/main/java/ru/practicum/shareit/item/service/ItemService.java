package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(long id, Item item);

    Item update(long userId, long itemId, ItemDto itemDto);

    Item getById(long id);

    List<Item> getAll(long userId);

    List<Item> searchText(long userId, String str);
}
