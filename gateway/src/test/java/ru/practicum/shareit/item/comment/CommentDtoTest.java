package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.CommentDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentDtoTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @DisplayName("Пустой коммент")
    @Test
    void createCommentDto_validateComment_whenTextIsBlank() {
        CommentDto commentDto = new CommentDto("");

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);
        assertEquals(1, violations.size(), "Создаётся пустой email");
    }

    @DisplayName("Null коммент")
    @Test
    void createCommentDto_validateComment_whenTextIsNull() {
        CommentDto commentDto = new CommentDto(null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);
        assertEquals(1, violations.size(), "Создаётся text null");
    }
}