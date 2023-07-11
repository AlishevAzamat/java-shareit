package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.ParameterNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public Item add(long id, Item item) {
        item.setOwner(userService.getById(id));
        log.info("Добавлен элемент {}", item);
        return itemRepository.add(item);
    }

    @Override
    public Item update(long userId, long itemId, ItemDto itemDto) {
        User user = userService.getById(userId);
        Item item = getById(itemId);
        if (item.getOwner().getId().equals(user.getId())) {
            updateName(item, itemDto);
            updateDescription(item, itemDto);
            updateAvailable(item, itemDto);
        } else {
            throw new ParameterNotFoundException("Вы не являетесь владельцем вещи");
        }
        log.info("Элемент {} обновлен", item);
        return item;
    }

    @Override
    public Item getById(long id) {
        validationId(id);
        log.info("Получен элемент {}", itemRepository.getById(id));
        return itemRepository.getById(id);
    }

    @Override
    public List<Item> getAll(long userId) {
        List<Item> items = new ArrayList<>();
        for (Item item : itemRepository.getAll()) {
            if (item.getOwner().getId().equals(userId)) {
                items.add(item);
            }
        }
        log.info("Получен список элементов - количество {}", itemRepository.getAll().size());
        return items;
    }

    @Override
    public List<Item> searchText(long userId, String str) {
        List<Item> items = new ArrayList<>();
        if (!str.isBlank()) {
            for (Item item : itemRepository.getAll()) {
                if (item.toString().toLowerCase().contains(str.toLowerCase()) && !Objects.equals(item.isAvailable(), false)) {
                    items.add(item);
                }
            }
        }
        log.info("Поиск элемента {}", str);
        log.info("Получен элемент {}", items);
        return items;
    }

    private void validationId(long id) {
        try {
            itemRepository.getById(id).isAvailable();
        } catch (NullPointerException e) {
            if (id < 0) {
                throw new IncorrectParameterException("id меньше 0");
            } else {
                throw new ParameterNotFoundException("id не найден");
            }
        }
    }

    private void updateName(Item item, ItemDto itemDto) {
        try {
            if (!itemDto.getName().isBlank()) {
                item.setName(itemDto.getName());
            }
        } catch (NullPointerException e) {
            log.error("Пустое поле");
            return;
        }
    }

    private void updateDescription(Item item, ItemDto itemDto) {
        try {
            if (!itemDto.getDescription().isBlank()) {
                item.setDescription(itemDto.getDescription());
            }
        } catch (NullPointerException e) {
            log.error("Пустое поле");
            return;
        }
    }

    private void updateAvailable(Item item, ItemDto itemDto) {
        try {
            itemDto.getAvailable().toString();
            item.setAvailable(itemDto.getAvailable());
        } catch (NullPointerException e) {
            log.error("Пустое поле");
            return;
        }
    }
}
