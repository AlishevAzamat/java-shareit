package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestDtoTest {
    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .created(LocalDateTime.now())
            .build();

    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validateFailDescriptionRequest() {
        requestDto.setDescription("");

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size(), "Создаётся пустой description");

        requestDto.setDescription(null);

        violations = validator.validate(requestDto);
        assertEquals(1, violations.size(), "Создаётся null description");
    }
}