package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    private User owner;
    @NotBlank(message = "Имя не должно быть пустым.")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым.")
    private String description;
    @NotNull
    private Boolean available;
    private Long request;
}
