package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.item.dto.ItemDtoWithRequestId;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemDtoWithRequestId add(Integer userId, ItemDtoWithRequestId itemDtoWithRequestId);

    ItemDto update(Integer itemId, Integer userId, ItemDto itemDto);

    ItemDto get(Integer itemId, Integer userId);

    List<ItemDtoExtended> getAll(Integer userId);

    List<ItemDto> search(Integer userId, String text);

    CommentOutputDto addComment(Integer itemId, Integer userId, Comment comment);
}
