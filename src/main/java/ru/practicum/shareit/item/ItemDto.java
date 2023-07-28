package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingItemDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    private User owner;
    @NotEmpty(message = "Имя не должно быть пустым.")
    private String name;
    @NotEmpty(message = "Описание не должно быть пустым.")
    private String description;
    @NotNull
    private Boolean available;
    private Long request;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private List<CommentDto> comments;
}
