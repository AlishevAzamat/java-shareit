package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

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
    private final ItemRequestService itemRequestService;

    @Override
    public ItemDto add(long id, ItemDto itemDto) {
        userService.getById(id);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userService.getById(id));
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestService.reply(itemDto.getRequestId()));
        }
        itemRepository.save(item);
        log.info("Добавлена вещь {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        User user = userService.getUser(userId);
        Item item = getItem(itemId);
        if (item.getOwner().getId().equals(user.getId())) {
            updateName(item, itemDto);
            updateDescription(item, itemDto);
            updateAvailable(item, itemDto);
            itemRepository.save(item);
            return itemMapper.toItemDto(item);
        } else {
            throw new ParameterNotFoundException(String.format("Вы не являетесь владельцем вещи под номером %d", itemId));
        }
    }

    @Override
    public Item getItem(long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ParameterNotFoundException("Пользователь не найден"));
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
        itemDto.setComments(commentRepository.findAllByItemId(item.getId())
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(toList()));
        return itemDto;
    }

    @Override
    public List<ItemDto> getAll(long userId, int from, int size) {
        int pageNumber = (int) Math.ceil((double) from / size);
        Page<Item> itemsPage = itemRepository.findByOwnerId(userId, PageRequest.of(pageNumber, size, Sort.by("id").ascending()));
        List<Item> items = itemsPage.toList();

        Map<Long, Booking> bookingsBeforeMap = bookingRepository.findByItemInAndStartBeforeOrderByStartDesc(items, LocalDateTime.now())
                .stream()
                .filter(booking -> booking.getItem() != null)
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity(), (b1, b2) -> b1));
        Map<Long, Booking> bookingsAfterMap = bookingRepository.findByItemInAndStartAfterAndStatusNotLikeOrderByStartAsc(items, LocalDateTime.now(), Status.REJECTED)
                .stream()
                .filter(booking -> booking.getItem() != null)
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity(), (b1, b2) -> b1));
        Map<Long, List<CommentDto>> commentsMap = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .filter(comment -> comment.getItem() != null)
                .collect(groupingBy(comment -> comment.getItem().getId(), Collectors.mapping(commentMapper::toCommentDto, Collectors.toList())));

        List<ItemDto> itemDtos = items
                .stream()
                .map(itemMapper::toItemDto)
                .peek(item -> {
                    item.setComments(commentsMap.getOrDefault(item.getId(), List.of()));
                    Optional.ofNullable(bookingsBeforeMap.get(item.getId()))
                            .ifPresent(booking -> item.setLastBooking(bookingMapper.toItemsBookingDto(booking)));
                    Optional.ofNullable(bookingsAfterMap.get(item.getId()))
                            .ifPresent(booking -> item.setNextBooking(bookingMapper.toItemsBookingDto(booking)));
                })
                .collect(toList());
        return itemDtos;
    }

    @Override
    public List<ItemDto> searchText(long userId, String text, int from, int size) {
        if (text.isBlank()) {
            return List.of();
        } else {
            int pageNumber = (int) Math.ceil((double) from / size);
            Page<Item> items = itemRepository.search(text, PageRequest.of(pageNumber, size));
            return items.stream()
                    .map(itemMapper::toItemDto)
                    .collect(toList());
        }
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        List<Booking> bookings =
                bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(itemId, userId, LocalDateTime.now(), Status.REJECTED);
        if (!bookings.isEmpty()) {
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
}
