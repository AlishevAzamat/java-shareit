package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequestDto {
    private long id;
    private User user;
    private String description;
    private LocalDate created;
}
