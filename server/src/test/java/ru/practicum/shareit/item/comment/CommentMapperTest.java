package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {
    private final CommentMapper commentMapper = new CommentMapper();

    @Test
    @DisplayName("Маппер toComment")
    void createComment_compareResult_toComment() {
        CommentDto commentDto = CommentDto.builder().text("text").build();
        User user = User.builder().id(1L).name("name").email("user@mail").build();
        Item item = Item.builder().name("name").description("desc").available(false).build();
        LocalDateTime time = LocalDateTime.now();
        Comment comment = commentMapper.toComment(user, item, commentDto, time);

        assertEquals(user, comment.getAuthor(), "user не сохроняет в model");
        assertEquals(item, comment.getItem(), "item не сохроняет в model");
        assertEquals(commentDto.getText(), comment.getText(), "text не сохроняет в model");
        assertEquals(time, comment.getCreated(), "created не сохроняется в model");
    }

    @Test
    @DisplayName("Маппер toCommentDto")
    void createComment_compareResult_toCommentDto() {
        Comment comment = Comment.builder()
                .created(LocalDateTime.now())
                .text("text")
                .author(User.builder().id(1L).name("name").email("user@mail").build())
                .id(1L)
                .build();
        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId(), "id не сохроняет в dto");
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName(), "user не сохроняет в dto");
        assertEquals(comment.getText(), commentDto.getText(), "text не сохроняет в dto");
        assertEquals(comment.getCreated(), commentDto.getCreated(), "created не сохроняется в dto");
    }
}