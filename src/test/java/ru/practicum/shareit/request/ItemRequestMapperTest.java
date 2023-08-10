package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemRequestMapperTest {
    private final ItemRequestMapper mapper = new ItemRequestMapper();

    @Test
    void toItemRequest() {
        RequestDto requestDto = RequestDto.builder()
                .description("description")
                .build();
        ItemRequest request = mapper.toItemRequest(requestDto);

        assertEquals(requestDto.getDescription(), request.getDescription(), "не сохроняет description в model");
        assertNull(request.getId(), "id в model не null");
        assertNull(request.getOwner(), "owner в model не null");
        assertNull(request.getCreated(), "created в model не null");
    }

    @Test
    void toItemRequestDto() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("description").build();
        ItemRequestDto requestDto = mapper.toItemRequestDto(request);

        assertEquals(request.getId(), requestDto.getId(), "id в model не null");
        assertEquals(requestDto.getDescription(), request.getDescription(), "не сохроняет description в model");
        assertEquals(request.getCreated(), requestDto.getCreated(), "created в model не null");
        assertNull(request.getOwner(), "owner в model не null");
    }

}