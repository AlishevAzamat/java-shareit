package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.validation.Create;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemDtoTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @DisplayName("Пустое имя предмета")
    @Test
    void createItemDto_validateItem_whenNameIsBlank() {
        ItemDto itemDto = new ItemDto(1L, "", "desc", true, 1L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся пустой name");
    }

    @DisplayName("Пустое описание предмета")
    @Test
    void createItemDto_validateItem_whenDescriptionIsBlank() {
        ItemDto itemDto = new ItemDto(1L, "name", "", true, 1L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся пустой description");
    }

    @DisplayName("Пустое имя предмета null")
    @Test
    void createItemDto_validateItem_whenNameIsNull() {
        ItemDto itemDto = new ItemDto(1L, null, "desc", true, 1L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся name null");
    }

    @DisplayName("Пустое описание предмета null")
    @Test
    void createItemDto_validateItem_whenDescriptionIsNull() {
        ItemDto itemDto = new ItemDto(1L, "name", null, true, 1L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся description null");
    }

    @DisplayName("Пустая доступность предмета")
    @Test
    void createItemDto_validateItem_whenAvailableIsNull() {
        ItemDto itemDto = new ItemDto(1L, "name", "desc", null, 1L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся available null");
    }
}