package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(Integer userId, ItemDto itemDto);

    ItemDto update(Integer itemId, Integer userId, ItemDto itemDto);

    ItemDto get(Integer itemId, Integer userId);

    List<ItemDtoExtended> getAll(Integer userId);

    List<ItemDto> search(Integer userId, String text);

    CommentOutputDto addComment(Integer itemId, Integer userId, Comment comment);

    ItemDtoExtended getItemWithComments(Integer itemId, Integer userId);
}
