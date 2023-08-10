package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {
    private final CommentMapper mapper = new CommentMapper();

    @Test
    void toComment() {
        CommentDto commentDto = CommentDto.builder().text("text").build();
        User user = User.builder().id(1L).name("name").email("user@mail").build();
        Item item = Item.builder().name("name").description("desc").available(false).build();
        LocalDateTime time = LocalDateTime.now();
        Comment comment = mapper.toComment(user, item, commentDto, time);

        assertEquals(user, comment.getAuthor(), "user не сохроняет в model");
        assertEquals(item, comment.getItem(), "item не сохроняет в model");
        assertEquals(commentDto.getText(), comment.getText(), "text не сохроняет в model");
        assertEquals(time, comment.getCreated(), "created не сохроняется в model");
    }

    @Test
    void toCommentDto() {
        Comment comment = Comment.builder()
                .created(LocalDateTime.now())
                .text("text")
                .author(User.builder().id(1L).name("name").email("user@mail").build())
                .id(1L)
                .build();
        CommentDto commentDto = mapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId(), "id не сохроняет в dto");
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName(), "user не сохроняет в dto");
        assertEquals(comment.getText(), commentDto.getText(), "text не сохроняет в dto");
        assertEquals(comment.getCreated(), commentDto.getCreated(), "created не сохроняется в dto");
    }
}