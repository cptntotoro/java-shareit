package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemDto toItemDto(Item item);

    Item toItem(ItemDto itemDto);
}
