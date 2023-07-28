package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingItemDto {
    private Long id;
    private Long bookerId;
}
