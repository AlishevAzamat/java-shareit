package shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    @DisplayName("Маппер toItemDtoWithoutRequestId")
    void createItem_compareResult_toItemDtoWithoutRequestId() {
        Item item = Item.builder()
                .id(1L)
                .owner(User.builder().id(1L).email("user@mail").name("name").build())
                .name("name")
                .description("description")
                .available(true)
                .build();
        ItemDto itemDto = itemMapper.toItemDto(item);

        assertEquals(item.getDescription(), itemDto.getDescription(), "description не присваивается в dto");
        assertEquals(item.getName(), itemDto.getName(), "name не присваивается в dto");
        assertEquals(item.isAvailable(), itemDto.getAvailable(), "available не присваивается в dto");
        assertNotNull(itemDto.getComments(), "не создаёт новый список comments");
        assertNull(itemDto.getRequestId(), "requestId не присваивается null в dto");
    }

    @Test
    @DisplayName("Маппер toItemDtoWithRequestId")
    void createItem_compareResult_toItemDtoWithRequestId() {
        Item item = Item.builder()
                .id(1L)
                .owner(User.builder().id(1L).email("user@mail").name("name").build())
                .name("name")
                .description("description")
                .available(true)
                .request(ItemRequest.builder().id(1L).description("description").build())
                .build();
        ItemDto itemDto = itemMapper.toItemDto(item);

        assertEquals(item.getDescription(), itemDto.getDescription(), "description не присваивается в dto");
        assertEquals(item.getName(), itemDto.getName(), "name не присваивается в dto");
        assertEquals(item.isAvailable(), itemDto.getAvailable(), "available не присваивается в dto");
        assertEquals(item.getRequest().getId(), itemDto.getRequestId(), "requestId не присваивается в dto");
        assertNotNull(itemDto.getComments(), "не создаёт новый список comments");
    }

    @Test
    @DisplayName("Маппер toItem")
    void createItem_compareResult_toItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(false)
                .build();
        Item item = itemMapper.toItem(itemDto);

        assertEquals(itemDto.getDescription(), item.getDescription(), "description не присваивается в model");
        assertEquals(itemDto.getName(), item.getName(), "name не присваивается в model");
        assertEquals(itemDto.getAvailable(), item.isAvailable(), "available не присваивается в model");
        assertNull(item.getOwner(), "owner в model не null");
        assertNull(item.getRequest(), "request в model не null");
    }
}