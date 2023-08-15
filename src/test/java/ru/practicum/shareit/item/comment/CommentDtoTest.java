package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentDtoTest {
    private final CommentDto commentDto = CommentDto.builder()
            .text("qweqw")
            .build();

    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    @DisplayName("Создание пустого комента")
    void setComment_compareResult_whenValidateFailDescriptionUser() {
        commentDto.setText("");

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);
        assertEquals(1, violations.size(), "Создаётся пустой email");

        commentDto.setText(null);

        violations = validator.validate(commentDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }
}