package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentMapperTest {

    private static Item item;

    private static User user;

    @BeforeAll
    static void setup() {
        item = new Item(1, "Balalaika", "Brand new balalaika", true);
        user = new User(2, "Shaun", "shaun@ya.ru");
    }

    @Test
    void toCommentOutputDto_FromComment() {
        Comment comment = new Comment(1,"Great balalaika! Thanks!", item, user, LocalDateTime.now().minusDays(2));

        CommentOutputDto commentOutputDto = CommentMapper.INSTANCE.toCommentOutputDto(comment);

        assertNotNull(commentOutputDto);

        assertEquals(comment.getId(), commentOutputDto.getId());
        assertEquals(comment.getText(), commentOutputDto.getText());
        assertEquals(comment.getAuthor().getName(), commentOutputDto.getAuthorName());
        assertEquals(comment.getCreated(), commentOutputDto.getCreated());
    }

}