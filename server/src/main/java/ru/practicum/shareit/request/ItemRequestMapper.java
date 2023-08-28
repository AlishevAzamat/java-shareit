package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ItemRequestMapper {
    public ItemRequest toItemRequest(RequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .build();
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(new ArrayList<>())
                .build();
    }
}
