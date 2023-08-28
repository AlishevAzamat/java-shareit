package shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.RequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemRequestMapperTest {
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    @Test
    @DisplayName("Маппер toItemRequest")
    void createRequest_compareResult_toItemRequest() {
        RequestDto requestDto = RequestDto.builder()
                .description("description")
                .build();
        ItemRequest request = itemRequestMapper.toItemRequest(requestDto);

        assertEquals(requestDto.getDescription(), request.getDescription(), "не сохроняет description в model");
        assertNull(request.getId(), "id в model не null");
        assertNull(request.getOwner(), "owner в model не null");
        assertNull(request.getCreated(), "created в model не null");
    }

    @Test
    @DisplayName("Маппер toItemRequestDto")
    void createRequest_compareResult_toItemRequestDto() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("description").build();
        ItemRequestDto requestDto = itemRequestMapper.toItemRequestDto(request);

        assertEquals(request.getId(), requestDto.getId(), "id в model не null");
        assertEquals(requestDto.getDescription(), request.getDescription(), "не сохроняет description в model");
        assertEquals(request.getCreated(), requestDto.getCreated(), "created в model не null");
        assertNull(request.getOwner(), "owner в model не null");
    }

}