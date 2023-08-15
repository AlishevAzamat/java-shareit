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
import java.util.stream.Collectors;

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
        State state = State.fromString(stateStr);
        Page<Booking> bookings = createBookingPageForUser(bookerId, state, from, size);

        return bookings
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private Page<Booking> createBookingPageForUser(long bookerId, State state, int from, int size) {
        int pageNumber = (from + size - 1) / size;
        PageRequest pageRequest = PageRequest.of(pageNumber, size, Sort.by("start").descending());

        switch (state) {
            case FUTURE:
                return bookingRepository.findByBookerIdAndStatusIn(bookerId, Set.of(Status.WAITING, Status.APPROVED), pageRequest);
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatusIs(bookerId, Status.REJECTED, pageRequest);
            case WAITING:
                return bookingRepository.findByBookerIdAndStatusIs(bookerId, Status.WAITING, pageRequest);
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                return bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(bookerId, now, now, pageRequest);
            case PAST:
                return bookingRepository.findByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), pageRequest);
            case ALL:
            default:
                return bookingRepository.findByBookerId(bookerId, pageRequest);
        }
    }


    @Override
    public List<BookingDto> getAllByOwner(long ownerId, String stateStr, int from, int size) {
        userService.getById(ownerId);
        State state = State.fromString(stateStr);
        Page<Booking> bookings = createBookingPage(ownerId, state, from, size);

        return bookings
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private Page<Booking> createBookingPage(long ownerId, State state, int from, int size) {
        int pageNumber = (from + size - 1) / size;
        PageRequest pageRequest = PageRequest.of(pageNumber, size, Sort.by("start").descending());

        switch (state) {
            case FUTURE:
                return bookingRepository.findByOwnerIdAndStatusIn(ownerId, Set.of(Status.WAITING, Status.APPROVED), pageRequest);
            case REJECTED:
                return bookingRepository.findByOwnerIdAndStatus(ownerId, Status.REJECTED, pageRequest);
            case WAITING:
                return bookingRepository.findByOwnerIdAndStatus(ownerId, Status.WAITING, pageRequest);
            case CURRENT:
                return bookingRepository.findByOwnerIdCurrent(ownerId, LocalDateTime.now(), pageRequest);
            case PAST:
                return bookingRepository.findByOwnerIdPast(ownerId, LocalDateTime.now(), pageRequest);
            case ALL:
            default:
                return bookingRepository.findByOwnerId(ownerId, pageRequest);
        }
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