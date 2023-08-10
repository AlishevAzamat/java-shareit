package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.ParameterNotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto create(long userId, BookingDto bookingDto) {
        Booking booking = bookingMapper.toBooking(bookingDto);
        userService.getById(userId);
        booking.setItem(itemService.getItem(bookingDto.getItemId()));
        if (booking.getItem().isAvailable()) {
            if (booking.getItem().getOwner().getId() != userId) {
                validateTime(booking.getStart(), booking.getEnd());
                booking.setBooker(userService.getById(userId));
                booking.setStatus(Status.WAITING);
                return bookingMapper.toBookingDto(bookingRepository.save(booking));
            } else {
                throw new IncorrectParameterException("Вы являетесь владельцем вещи - бронирование невозможно.");
            }
        } else {
            throw new ValidationException("Эта вещь уже забронирована.");
        }
    }

    @Override
    public BookingDto update(long userId, long bookingId, Boolean approved) {
        if (Optional.ofNullable(approved).isPresent()) {
            Booking booking = getBooking(bookingId);
            if (booking.getItem().getOwner().getId() == userId) {
                if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
                    throw new ValidationException("Вы уже подвертили или отказали бронирование. Повторное действие не возможно.");
                }
                if (approved) {
                    booking.setStatus(Status.APPROVED);
                } else {
                    booking.setStatus(Status.REJECTED);
                }
                bookingRepository.save(booking);
                return bookingMapper.toBookingDto(booking);
            } else {
                throw new ParameterNotFoundException("Вы не являетесь владельцем вещи.");
            }
        } else {
            throw new UnknownStateException("Обязательно должен быть указан approved");
        }
    }

    @Override
    public BookingDto getById(long userId, long bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return bookingMapper.toBookingDto(booking);
        } else {
            throw new IncorrectParameterException("Вы не являетесь автором бронирования или владельцем вещи.");
        }
    }

    @Override
    public List<BookingDto> getAllByUser(long bookerId, String stateStr, int from, int size) {
        userService.getById(bookerId);
        Page<Booking> bookings;
        int pageNumber = (int) Math.ceil((double) from / size);
        State state = State.fromString(stateStr);
        switch (state) {
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStatusIn(bookerId, Set.of(Status.WAITING, Status.APPROVED),
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusIs(bookerId, Status.REJECTED,
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusIs(bookerId, Status.WAITING,
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now(),
                        LocalDateTime.now(), PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBefore(bookerId, LocalDateTime.now(),
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case ALL:
            default:
                bookings = bookingRepository.findByBookerId(bookerId, PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
        }
        return bookings
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(long ownerId, String stateStr, int from, int size) {
        userService.getById(ownerId);
        Page<Booking> bookings;
        int pageNumber = (int) Math.ceil((double) from / size);
        State state = State.fromString(stateStr);
        switch (state) {
            case FUTURE:
                bookings = bookingRepository.findByOwnerIdAndStatusIn(ownerId, Set.of(Status.WAITING, Status.APPROVED),
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerIdAndStatus(ownerId, Status.REJECTED,
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerIdAndStatus(ownerId, Status.WAITING,
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case CURRENT:
                bookings = bookingRepository.findByOwnerIdCurrent(ownerId, LocalDateTime.now(),
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case PAST:
                bookings = bookingRepository.findByOwnerIdPast(ownerId, LocalDateTime.now(),
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case ALL:
            default:
                bookings = bookingRepository.findByOwnerId(ownerId, PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
        }
        return bookings
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(toList());
    }

    private Booking getBooking(long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isEmpty()) {
            if (id < 0) {
                throw new IncorrectParameterException("id не должно быть меньше 0.");
            } else {
                throw new ParameterNotFoundException(String.format("Бронирования с id %d - не существует.", id));
            }
        } else {
            return booking.get();
        }
    }

    private void validateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ValidationException("Пустое врумя");
        }
        if (end.isBefore(start)) {
            throw new ValidationException("Конец бронирования не может быть раньше старта.");
        }
        if (end.equals(start)) {
            throw new ValidationException("Конец бронирования не может быть одинаков с стартом.");
        }
        LocalDateTime time = LocalDateTime.now().withSecond(0);
        if (start.isBefore(time)) {
            throw new ValidationException("Начало бронирования может начатся только с текущего времени.");
        }
    }
}