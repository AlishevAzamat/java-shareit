package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "item_requests")
@Data
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
    private String description;
    @Column(name = "created_date")
    private LocalDate created;
}
