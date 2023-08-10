package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentTest {
    Comment comment = Comment.builder().id(1L).build();

    @Test
    void testEquals() {
        assertTrue(comment.equals(Comment.builder().id(1L).build()));
    }

    @Test
    void testHashCode() {
        Comment comment1 = Comment.builder().id(1L).build();
        assertEquals(comment1.hashCode(), comment.hashCode());
    }
}