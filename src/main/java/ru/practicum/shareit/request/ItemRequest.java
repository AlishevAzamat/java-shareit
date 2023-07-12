package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequest {
    private long id;
    private User user;
    private String description;
    private LocalDate created;
}
