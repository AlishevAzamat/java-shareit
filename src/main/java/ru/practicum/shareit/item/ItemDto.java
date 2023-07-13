package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    private UserDto owner;
    @NotEmpty(message = "Имя не должно быть пустым.")
    private String name;
    @NotEmpty(message = "Описание не должно быть пустым.")
    private String description;
    @NotNull
    private Boolean available;
    private Long request;
}
