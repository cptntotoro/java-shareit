package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    UserService userService;
    @Autowired
    ItemMapper itemMapper;
    @Autowired
    UserMapper userMapper;

    private final Map<Integer, Item> items = new HashMap<>();
    private int idGenerator = 0;

    @Override
    public ItemDto add(Integer userId, ItemDto itemDto) {
        validateItemDto(itemDto);

        if (!userWithIdExists(userId)) {
            throw new UserDoesNotExistException("Failed to create item. User with id " + userId + " was not found.");
        }

        Item item = itemMapper.itemDtoToItem(itemDto);
        item.setOwner(userMapper.userDtoToUser(userService.get(userId)));
        item.setId(++idGenerator);
        items.put(item.getId(), item);
        return itemMapper.itemToItemDto(item);
    }

    @Override
    public ItemDto update(Integer itemId, Integer userId, ItemDto itemDto) {
        if (itemId.equals(itemDto.getId()) && itemWithIdExists(itemId)) {
            if (!userWithIdExists(userId)) {
                throw new UserDoesNotExistException("Failed to create item. User with id " + userId + " was not found.");
            }
        }

        Item item = items.get(itemId);

        if (!Objects.equals(userId, item.getOwner().getId())) {
            throw new ItemAccessException("Failed to update item. Only item owners are allowed to update items.");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setIsAvailable(itemDto.getAvailable());
        }

        items.put(item.getId(), item);
        return itemMapper.itemToItemDto(items.get(itemId));
    }

    @Override
    public ItemDto get(Integer itemId) {
        if (!itemWithIdExists(itemId)) {
            throw new ItemDoesNotExistException("Failed to get item. Item id is not found.");
        }
        return itemMapper.itemToItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getAll(Integer userId) {
        List<Item> userItems = items.values().stream().filter(item -> Objects.equals(item.getOwner().getId(), userId)).collect(Collectors.toList());
        return userItems.stream().map(itemMapper::itemToItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(Integer userId, String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        String searchQuery = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getIsAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchQuery)
                        || item.getDescription().toLowerCase().contains(searchQuery))
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getName() == null || itemDto.getName().isEmpty() || itemDto.getDescription() == null) {
            throw new ItemDtoIntegrityException("Failed to process request. Item's name, description or isAvailable status must not be null.");
        }
    }

    private boolean userWithIdExists(Integer userId) {
        return userService.getAll().stream().anyMatch(userDto -> userDto.getId().equals(userId));
    }

    private boolean itemWithIdExists(Integer itemId) {
        return items.values().stream().anyMatch(item -> item.getId().equals(itemId));
    }
}
