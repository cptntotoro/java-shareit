package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(Integer userId, ItemDto itemDto);

    ItemDto update(Integer itemId, Integer userId, ItemDto itemDto);

    ItemDto get(Integer itemId);

    List<ItemDto> getAll(Integer userId);

    List<ItemDto> search(Integer userId, String text);
}
