package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не должно быть пустым.")
    private String name;
    @Email(message = "Введён не email.")
    @NotBlank(message = "Email не должно быть пустым.")
    private String email;
}
