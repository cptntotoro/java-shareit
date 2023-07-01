package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    @Test
    void toItemDto_FromItem() {
        Item item = new Item(1, "Balalaika", "Brand new balalaika", true);

        ItemDto itemDto = ItemMapper.INSTANCE.toItemDto(item);

        assertNotNull(itemDto);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void toItemDtoWithRequestId_FromItem() {
        User userRequesting = new User(2, "Shaun", "shaun@ya.ru");
        Request request = new Request(1, "Looking for a balalaika", LocalDateTime.now(), userRequesting);
        User owner = new User(1, "Jason", "jason@ya.ru");
        Item item = new Item(1, "Balalaika", "Brand new balalaika", true, owner, request);

        ItemDtoWithRequestId itemDtoWithRequestId = ItemMapper.INSTANCE.toItemDtoWithRequestId(item);

        assertNotNull(itemDtoWithRequestId);

        assertEquals(item.getId(), itemDtoWithRequestId.getId());
        assertEquals(item.getName(), itemDtoWithRequestId.getName());
        assertEquals(item.getDescription(), itemDtoWithRequestId.getDescription());
        assertEquals(item.getAvailable(), itemDtoWithRequestId.getAvailable());
        assertNull(itemDtoWithRequestId.getRequestId());
    }

    @Test
    void toItem_FromItemDto() {
        ItemDto itemDto = new ItemDto(1, "Balalaika", "Brand new balalaika", true);
        Item item = ItemMapper.INSTANCE.toItem(itemDto);

        assertNotNull(item);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @Test
    void toItem_FromItemDtoWithRequestId() {
        ItemDto itemDto = new ItemDto(1, "Balalaika", "Brand new balalaika", true);
        ItemDtoWithRequestId itemDtoWithRequestId = new ItemDtoWithRequestId(itemDto, 1);

        Item item = ItemMapper.INSTANCE.toItem(itemDtoWithRequestId);

        assertNotNull(item);

        assertEquals(itemDtoWithRequestId.getId(), item.getId());
        assertEquals(itemDtoWithRequestId.getName(), item.getName());
        assertEquals(itemDtoWithRequestId.getDescription(), item.getDescription());
        assertEquals(itemDtoWithRequestId.getAvailable(), item.getAvailable());
        assertNull(item.getRequest());
    }
}