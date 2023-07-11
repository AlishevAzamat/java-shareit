package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        User user = userService.getById(userId);
        return itemMapper.toItemDtoUser(itemService.add(userId, item), user);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        User user = userService.getById(userId);
        return itemMapper.toItemDtoUser(itemService.update(userId, itemId, itemDto), user);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable long itemId) {
        Long item = itemService.getById(itemId).getOwner().getId();
        User user = userService.getById(item);
        return itemMapper.toItemDtoUser(itemService.getById(itemId), user);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAll(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text) {
        return itemService.searchText(userId, text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }
}
