package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestDtoTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @DisplayName("Пустое описание")
    @Test
    void createRequestDto_validateRequest_whenDescriptionIsBlank() {
        RequestDto requestDto = new RequestDto("");

        Set<ConstraintViolation<RequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size(), "Создаётся пустой description");
    }

    @DisplayName("Пустое описание null")
    @Test
    void createRequestDto_validateRequest_whenDescriptionIsNull() {
        RequestDto requestDto = new RequestDto(null);

        Set<ConstraintViolation<RequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size(), "Создаётся пустой description");
    }
}