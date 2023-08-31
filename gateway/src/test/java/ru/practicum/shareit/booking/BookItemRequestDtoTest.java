package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookItemRequestDtoTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @DisplayName("Создание обьекта с id -1")
    @Test
    void createBookItemRequestDto_validateBooker_whenItemIdIsNegative() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(-1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @DisplayName("Создание обьекта с id 0")
    @Test
    void createBookItemRequestDto_validateBooker_whenItemIdIsZero() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(0L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @DisplayName("Создание обьекта с id null")
    @Test
    void createBookItemRequestDto_validateBooker_whenItemIdIsNull() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @DisplayName("Создание обьекта когда некорректное время")
    @Test
    void createBookItemRequestDto_validateBooker_whenStartIsPast() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @DisplayName("Создание обьекта когда некорректное время")
    @Test
    void createBookItemRequestDto_validateBooker_whenStartIsNull() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, null, LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @DisplayName("Создание обьекта когда некорректное время")
    @Test
    void createBookItemRequestDto_validateBooker_whenEndIsPresent() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now());

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @DisplayName("Создание обьекта когда некорректное время")
    @Test
    void createBookItemRequestDto_validateBooker_whenEndIsPast() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusMonths(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @DisplayName("Создание обьекта когда некорректное время")
    @Test
    void createBookItemRequestDto_validateBooker_whenEndIsNull() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), null);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }
}