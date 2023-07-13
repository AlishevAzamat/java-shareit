package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto add(long id, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto getById(long id);

    List<ItemDto> getAll(long userId);

    List<ItemDto> searchText(long userId, String str);
}
