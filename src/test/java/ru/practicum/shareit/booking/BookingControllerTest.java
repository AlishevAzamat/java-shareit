package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Создания бронирования")
    void createBooking_compareResult_whenObjectCurrent() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.registerModule(new JavaTimeModule())
                                .writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    @DisplayName("Обновление бронирования")
    void updateBooking_compareResult_whenObjectCurrent() throws Exception {
        bookingDto.setItemId(1L);

        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.registerModule(new JavaTimeModule())
                                .writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class));
    }

    @Test
    @DisplayName("Запрос бронирования")
    void getBooking_compareResult_whenObjectCurrent() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    @DisplayName("Запрос бронирования пользователя")
    void getAllBookingByUser_compareResult_whenObjectCurrent() throws Exception {
        bookingDto.setStart(null);
        bookingDto.setEnd(null);
        when(bookingService.getAllByUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.registerModule(new JavaTimeModule())
                                .writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    @DisplayName("Запрос бронирования хозяина")
    void getAllBookingByOwner_compareResult_whenObjectCurrent() throws Exception {
        bookingDto.setStart(null);
        bookingDto.setEnd(null);
        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.registerModule(new JavaTimeModule())
                                .writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    @DisplayName("Запрос бронирования хозяина пагинация 0")
    void getAllBookingByOwner_throwException_whenSizeZero() throws Exception {
        mvc.perform(get("/bookings/owner?from=0&size=0")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.registerModule(new JavaTimeModule())
                                .writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Запрос бронирования хозяина пагинация -1")
    void getAllBookingByOwner_throwException_whenSizeNegative() throws Exception {
        mvc.perform(get("/bookings/owner?from=0&size=-1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.registerModule(new JavaTimeModule())
                                .writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Запрос бронирования хозяина 0")
    void getAllBookingByOwner_throwException_whenFromNegative() throws Exception {
        mvc.perform(get("/bookings/owner?from=-1&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.registerModule(new JavaTimeModule())
                                .writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Запрос бронирования пользователя пагинация 0")
    void getAllBookingByUser_throwException_whenSizeZero() throws Exception {
        mvc.perform(get("/bookings?from=0&size=0")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.registerModule(new JavaTimeModule())
                                .writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Запрос бронирования пользователя пагинация -1")
    void getAllBookingByUser_throwException_whenSizeNegative() throws Exception {
        mvc.perform(get("/bookings?from=0&size=-1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.registerModule(new JavaTimeModule())
                                .writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Запрос бронирования пользователя -1")
    void getAllBookingByUser_throwException_whenFromNegative() throws Exception {
        mvc.perform(get("/bookings?from=-1&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.registerModule(new JavaTimeModule())
                                .writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}