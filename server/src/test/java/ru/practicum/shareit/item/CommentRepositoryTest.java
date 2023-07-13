package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByItemId() {
        Item item = new Item();
        item.setName("Balalaika");
        item.setDescription("Brand new balalaika");
        item.setAvailable(true);
        Item itemSaved = itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("Great balalaika! Thanks!");
        comment.setItem(item);
        commentRepository.save(comment);

        List<Comment> commentsSaved = commentRepository.findByItemId(itemSaved.getId());

        assertNotNull(commentsSaved);
        assertEquals(1, commentsSaved.size());

        Comment commentSaved = commentsSaved.get(0);

        assertNotNull(commentSaved);
        assertEquals(itemSaved.getId(), commentSaved.getItem().getId());
    }
}