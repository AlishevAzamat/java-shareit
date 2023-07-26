package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<List<Booking>> findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(Long itemId, Long bookerId, LocalDateTime time, Status status);

    Optional<Booking> findFirst1ByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime time);

    Optional<Booking> findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(Long itemId, LocalDateTime time, Status status);

    List<Booking> findByBookerIdAndStatusInOrderByStartDesc(Long bookerId, Set<Status> states);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusIsOrderByStartDesc(Long bookerId, Status state);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime start);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 and b.status in (?2) " +
            "order by b.start desc")
    List<Booking> findByOwnerIdAndStatusIn(Long ownerId, Set<Status> states);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerIdAndStatus(Long ownerId, Status state);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findByOwnerId(Long ownerId);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 and b.start < ?2 and b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerIdCurrent(Long ownerId, LocalDateTime time);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerIdPast(Long ownerId, LocalDateTime start);
}