package ru.practicum.shareit.item.comment;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Item;

import javax.transaction.Transactional;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Transactional
    List<Comment> findAllByItemId(Long itemId);

    List<Comment> findByItemIn(List<Item> items, Sort created);
}