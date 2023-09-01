package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(Long itemId, Long bookerId, LocalDateTime time, Status status);

    Optional<Booking> findFirst1ByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime time);

    Optional<Booking> findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(Long itemId, LocalDateTime time, Status status);

    List<Booking> findByItemInAndStartBeforeOrderByStartDesc(List<Item> items, LocalDateTime time);

    List<Booking> findByItemInAndStartAfterAndStatusNotLikeOrderByStartAsc(List<Item> items, LocalDateTime time, Status status);

    Page<Booking> findByBookerIdAndStatusIn(Long bookerId, Set<Status> states, Pageable pageable);

    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusIs(Long bookerId, Status state, Pageable pageable);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime start, Pageable pageable);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 and b.status in (?2) ")
    Page<Booking> findByOwnerIdAndStatusIn(Long ownerId, Set<Status> states, Pageable pageable);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 and b.status = ?2 ")
    Page<Booking> findByOwnerIdAndStatus(Long ownerId, Status state, Pageable pageable);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 ")
    Page<Booking> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 and b.start < ?2 and b.end > ?2 ")
    Page<Booking> findByOwnerIdCurrent(Long ownerId, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 and b.end < ?2 ")
    Page<Booking> findByOwnerIdPast(Long ownerId, LocalDateTime start, Pageable pageable);
}