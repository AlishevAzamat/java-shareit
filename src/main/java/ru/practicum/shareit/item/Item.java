package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {
    private long id;
    private User owner;
    @NotBlank(message = "Имя не должно быть пустым.")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым.")
    private String description;
    private boolean available;
    private ItemRequest request;
}
