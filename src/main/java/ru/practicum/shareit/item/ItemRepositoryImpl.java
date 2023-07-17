package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private long id = 1;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item add(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(long id) {
        items.remove(id);
    }

    @Override
    public Item getById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getAllByOwnerId(long id) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(id)) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public List<Item> findByText(long userId, String str) {
        return items.values().stream()
                .filter(item -> item.toString().toLowerCase().contains(str.toLowerCase()) && item.isAvailable())
                .collect(Collectors.toList());
    }
}
