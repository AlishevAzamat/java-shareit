package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Transactional
    Optional<List<Comment>> findAllByItemId(Long itemId);
}