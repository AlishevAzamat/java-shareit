package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ParameterNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto add(long id, ItemDto itemDto) {
        User user = userService.getById(id);
        Item item = itemMapper.toItem(itemDto);
        if (user == null) {
            throw new ParameterNotFoundException("Пользователь не найден");
        } else {
            item.setOwner(userService.getById(id));
            itemRepository.add(item);
        }
        log.info("Добавлена вещь {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        User user = userService.getById(userId);
        Item item = itemRepository.getById(itemId);
        if (item.getOwner().getId().equals(user.getId())) {
            updateName(item, itemDto);
            updateDescription(item, itemDto);
            updateAvailable(item, itemDto);
        } else {
            throw new ParameterNotFoundException("Вы не являетесь владельцем вещи");
        }
        log.info("Вещь обновлена {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(long id) {
        ItemDto itemDto = itemMapper.toItemDto(itemRepository.getById(id));
        if (itemDto == null) {
            throw new ParameterNotFoundException("Данной вещи нет");
        }
        log.info("Получена вещь {}", itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        List<ItemDto> itemDto = new ArrayList<>();
        for (Item item : itemRepository.getAllByOwnerId(userId)) {
            ItemDto itemToDto = itemMapper.toItemDto(item);
            itemDto.add(itemToDto);
        }
        log.info("Получен список вещей - количество {}", itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> searchText(long userId, String str) {
        List<ItemDto> itemDto = new ArrayList<>();
        for (Item item : itemRepository.findByText(userId, str)) {
            ItemDto itemToDto = itemMapper.toItemDto(item);
            itemDto.add(itemToDto);
        }
        log.info("Поиск вещи {}", str);
        log.info("Получена вещь {}", itemDto);
        return itemDto;
    }

    private void updateName(Item item, ItemDto itemDto) {
        if (itemDto.getName() == null) {
            return;
        } else {
            item.setName(itemDto.getName());
        }
    }

    private void updateDescription(Item item, ItemDto itemDto) {
        if (itemDto.getDescription() == null) {
            return;
        } else {
            item.setDescription(itemDto.getDescription());
        }
    }

    private void updateAvailable(Item item, ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            return;
        }
        item.setAvailable(itemDto.getAvailable());
    }
}