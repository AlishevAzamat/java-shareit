package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @DisplayName("Пустое имя")
    @Test
    void createUserDto_validateCreate_whenUserNameIsBlank() {
        UserDto userDto = new UserDto(1L, "", "nik@mail.ru");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        violations.forEach(ConstraintViolation::getMessage);
        assertEquals(1, violations.size(), "Создаётся name с пустой строкой");
    }

    @DisplayName("Пустая почта")
    @Test
    void createUserDto_validateCreate_whenUserEmailIsBlank() {
        UserDto userDto = new UserDto(1L, "name", "");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся email с пустой строкой");
    }

    @DisplayName("Пустое имя null")
    @Test
    void createUserDto_validateCreate_whenUserNameIsNull() {
        UserDto userDto = new UserDto(1L, null, "nik@mail.ru");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся name с null");
    }

    @DisplayName("Пустая почта null")
    @Test
    void createUserDto_validateCreate_whenUserEmailIsNull() {
        UserDto userDto = new UserDto(1L, "name", null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся email с null");
    }

    @DisplayName("Некорректный email")
    @Test
    void createUserDto_validateUpdate_whenUserNoEmail() {
        UserDto userDto = new UserDto(1L, "name", "@");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Update.class);
        assertEquals(1, violations.size(), "Создаётся не email");
    }

    @DisplayName("Некорректный email null")
    @Test
    void createUserDto_validateCreate_whenUserNoEmail() {
        UserDto userDto = new UserDto(1L, "name", null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся не email");
    }
}