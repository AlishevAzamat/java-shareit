package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.ParameterNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto add(long id, ItemDto itemDto) {
        User user = userService.getById(id);
        Item item = itemMapper.toItem(itemDto);
        if (user == null) {
            throw new ParameterNotFoundException("Пользователь не найден");
        } else {
            item.setOwner(userService.getById(id));
            itemRepository.save(item);
        }
        log.info("Добавлена вещь {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        User user = userService.getById(userId);
        Item item = getItem(itemId);
        if (item.getOwner().getId().equals(user.getId())) {
            updateName(item, itemDto);
            updateDescription(item, itemDto);
            updateAvailable(item, itemDto);
            itemRepository.save(item);
        } else {
            throw new ParameterNotFoundException("Вы не являетесь владельцем вещи");
        }
        log.info("Вещь обновлена {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public Item getItem(long id) {
        Optional<Item> optional = itemRepository.findById(id);
        if (optional.isEmpty()) {
            throw new ParameterNotFoundException(String.format("Вещь с id %d - не существует.", id));
        } else {
            return optional.get();
        }
    }

    @Override
    public ItemDto getById(long id, long userId) {
        Item item = getItem(id);
        ItemDto itemDto = itemMapper.toItemDto(item);
        if (itemDto.getOwner().getId().equals(userId)) {
            bookingRepository.findFirst1ByItemIdAndStartBeforeOrderByStartDesc(id, LocalDateTime.now())
                    .ifPresent(booking -> itemDto.setLastBooking(bookingMapper.toItemsBookingDto(booking)));
            bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(id, LocalDateTime.now(), Status.REJECTED)
                    .ifPresent(booking -> itemDto.setNextBooking(bookingMapper.toItemsBookingDto(booking)));
        }
        itemDto.setComments(commentRepository.findAllByItemId(item.getId()).orElse(List.of())
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(toList()));
        return itemDto;
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = itemMapper.toItemDto(item);
            bookingRepository.findFirst1ByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now())
                    .ifPresent(booking -> itemDto.setLastBooking(bookingMapper.toItemsBookingDto(booking)));
            bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(item.getId(), LocalDateTime.now(), Status.REJECTED)
                    .ifPresent(booking -> itemDto.setNextBooking(bookingMapper.toItemsBookingDto(booking)));
            itemDto.setComments(commentRepository.findAllByItemId(item.getId()).orElse(List.of())
                    .stream()
                    .map(commentMapper::toCommentDto)
                    .collect(toList()));
            itemsDto.add(itemDto);
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> searchText(long userId, String str) {
        if (str.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = findByText(userId, str);
        log.info("Поиск вещи {}", str);
        log.info("Получена вещь {}", items);
        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        Optional<List<Booking>> bookings =
                bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(itemId, userId, LocalDateTime.now(), Status.REJECTED);
        if (bookings.isPresent() && !bookings.get().isEmpty()) {
            Comment comment = commentMapper.toComment(userService
                    .getById(userId), getItem(itemId), commentDto, LocalDateTime.now());
            return commentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new ValidationException("Вы не бронировали эту вещь или срок бронирвания ещё не истёк.");
        }
    }

    private void updateName(Item item, ItemDto itemDto) {
        if (itemDto.getName() == null) {
            return;
        } else {
            item.setName(itemDto.getName());
        }
    }

    private void updateDescription(Item item, ItemDto itemDto) {
        if (itemDto.getDescription() == null) {
            return;
        } else {
            item.setDescription(itemDto.getDescription());
        }
    }

    private void updateAvailable(Item item, ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            return;
        }
        item.setAvailable(itemDto.getAvailable());
    }

    private List<Item> findByText(long userId, String str) {
        return itemRepository.findAll().stream()
                .filter(item -> item.toString().toLowerCase().contains(str.toLowerCase()) && item.isAvailable())
                .collect(toList());
    }
}
