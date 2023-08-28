package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto add(long userId, RequestDto requestDto);

    ItemRequestDto getById(long userId, long requestId);

    List<ItemRequestDto> getAllByUser(long userId, int from, int size);

    List<ItemRequestDto> getAll(long userId, int from, int size);

    ItemRequest reply(long requestId);
}
