package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;

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
        return findByOwnerId(id);
    }

    @Override
    public List<Item> findByText(long userId, String str) {
        List<Item> userItems = new ArrayList<>();
        if (str.isBlank()) {
            return new ArrayList<>();
        } else {
            for (Item item : items.values()) {
                if (item.toString().toLowerCase().contains(str.toLowerCase()) && !Objects.equals(item.isAvailable(), false)) {
                    userItems.add(item);
                }
            }
        }
        return userItems;
    }

    private List<Item> findByOwnerId(long userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                userItems.add(item);
            }
        }
        return userItems;
    }
}
