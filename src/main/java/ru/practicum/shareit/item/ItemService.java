package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

public interface ItemService {
    ItemDto add(long id, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    Item getItem(long id);

    ItemDto getById(long id, long userId);

    List<ItemDto> getAll(long userId);

    List<ItemDto> searchText(long userId, String str);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
